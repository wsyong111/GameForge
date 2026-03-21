package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio.decode;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import io.github.wsyong11.gameforge.framework.system.audio.audio.pcm.PCMArrayList;
import io.github.wsyong11.gameforge.framework.system.audio.audio.pcm.PCMList;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.concurrent.signal.Notifier;
import io.github.wsyong11.gameforge.util.concurrent.signal.ValueNotifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.formatTime;

public class DecodeTask implements Runnable, Closeable {
	private static final Logger LOGGER = Log.getLogger();

	private final Identifier id;

	private final long idleTimeout;
	private final TimeUnit idleTimeoutUnit;

	private final ValueNotifier<Integer> targetDecodeSize;
	private final Notifier newDataNotifier;

	private final PCMList pcmData;
	private volatile FloatBuffer tempBuffer;
	private volatile AudioDecoder decoder;
	private volatile AudioMetadata metadata;

	private final AtomicReference<DecodeState> state;

	private volatile Throwable lastException;

	public DecodeTask(
		@NotNull Identifier id,
		@NotNull AudioMetadata metadata,
		@NotNull AudioDecoder decoder,
		int bufferDurationSec,
		long idleTimeout,
		@NotNull TimeUnit idleTimeoutUnit
	) {
		Objects.requireNonNull(id, "id is null");
		Objects.requireNonNull(metadata, "metadata is null");
		Objects.requireNonNull(idleTimeoutUnit, "idleTimeoutUnit is null");

		this.id = id;

		this.idleTimeout = idleTimeout;
		this.idleTimeoutUnit = idleTimeoutUnit;

		this.targetDecodeSize = new ValueNotifier<>(0);
		this.newDataNotifier = new Notifier();

		this.pcmData = new PCMArrayList(metadata.getFrameRate(), metadata.getChannels());

		int tempBufferSize = metadata.getSamplesPerSecond() * bufferDurationSec;
		this.tempBuffer = FloatBuffer.allocate(tempBufferSize);

		this.metadata = metadata;
		this.decoder = decoder;

		this.state = new AtomicReference<>(DecodeState.IDLE);

		this.lastException = null;
	}

	@Nullable
	public Throwable getLastException() {
		return this.lastException;
	}

	@NotNull
	public DecodeState getState() {
		return this.state.get();
	}

	private void doRun() throws AudioDecodeException {
		Thread currentThread = Thread.currentThread();

		int channels = this.metadata.getChannels();

		while (!currentThread.isInterrupted()) {
			LOGGER.verbose("[{}] Waiting decode request", this.id);
			try {
				if (!this.targetDecodeSize.awaitUntil(
					(size) -> this.pcmData.getFrames() < size,
					this.idleTimeout,
					this.idleTimeoutUnit
				)) {
					this.state.set(DecodeState.IDLE);
					break;
				}
			} catch (InterruptedException e) {
				currentThread.interrupt();
				this.state.set(DecodeState.IDLE);
				break;
			}

			long startTime = System.nanoTime();
			LOGGER.verbose("[{}] Begin decode", this.id);
			this.tempBuffer.clear();

			int len;
			try {
				len = this.decoder.decode(this.tempBuffer, this.tempBuffer.remaining() / channels);
			} catch (AudioDecodeException e) {
				LOGGER.error("Failed to decode pcm data", e);
				throw e;
			}

			if (len == -1) {
				this.state.set(DecodeState.COMPLETE);
				this.pcmData.trim();
				this.tempBuffer = null;
				break;
			}

			this.tempBuffer.flip();
			this.pcmData.add(this.tempBuffer);
			this.newDataNotifier.signal();

			long usedTime = System.nanoTime() - startTime;
			LOGGER.verbose("[{}] Decoded {} frames, took {}",
				this.id,
				len,
				formatTime(usedTime, TimeUnit.NANOSECONDS));

		}
	}

	@Override
	public void run() {
		if (!this.state.compareAndSet(DecodeState.IDLE, DecodeState.DECODING))
			return;

		LOGGER.trace("[{}] Start decode task", this.id);

		this.lastException = null;
		try {
			this.doRun();
		} catch (Throwable e) {
			LOGGER.error("[{}] An error occurred while decoding", this.id, e);
			this.lastException = e;
			this.state.set(DecodeState.FAILED);
		} finally {
			// 避免 doRun 没有正确切换状态
			if (this.state.compareAndSet(DecodeState.DECODING, DecodeState.IDLE))
				LOGGER.warn("[{}] State switched from DECODING to IDLE in finally", this.id);

			LOGGER.trace("[{}] Decode task exited", this.id);
		}
	}

	@Override
	public void close() {
		if (this.state.getAndSet(DecodeState.CLOSED) == DecodeState.CLOSED)
			return;

		this.decoder = null;
		this.metadata = null;

		this.pcmData.clear();
		this.pcmData.trim();

		this.tempBuffer.clear();
		this.tempBuffer = null;
	}

	public enum DecodeState {
		IDLE,
		DECODING,
		COMPLETE,
		FAILED,
		CLOSED
	}
}
