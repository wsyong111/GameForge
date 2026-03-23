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
import io.github.wsyong11.gameforge.util.exception.ExceptionUtils;
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

	private final Object pcmDataLock;
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

		this.pcmDataLock = new Object();
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
					(size) -> this.pcmData.getFrames() <= size,
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
				LOGGER.verbose("[{}] Decode complete", this.id);
				this.state.set(DecodeState.COMPLETE);
				this.pcmData.trim();
				this.tempBuffer = null;
				break;
			}

			this.tempBuffer.flip();

			synchronized (this.pcmDataLock) {
				int added = this.pcmData.add(this.tempBuffer, len, this.pcmData.getFrames());
				assert added == len;
			}

			this.newDataNotifier.signal();

			long usedTime = System.nanoTime() - startTime;
			LOGGER.verbose("[{}] Decoded {} frames (total {}), took {}",
				this.id,
				len,
				this.pcmData.getFrames(),
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

	public int getBufferSize() {
		return this.pcmData.getFrames();
	}

	public void ensureData(int position) {
		if (position < 0)
			throw new IllegalArgumentException("Position cannot be negative");

		DecodeState state = this.state.get();
		if (state == DecodeState.FAILED || state == DecodeState.CLOSED)
			return;

		long totalFramesLong = this.metadata.getTotalFrames();
		int totalFrames = (int) Math.min(totalFramesLong, Integer.MAX_VALUE);
		this.targetDecodeSize.update(v -> Math.max(v, Math.min(position, totalFrames)));
	}

	public int readData(int position, @NotNull FloatBuffer buffer, int maxFrames) throws AudioDecodeException, InterruptedException {
		Objects.requireNonNull(buffer, "buffer is null");

		if (this.state.get() == DecodeTask.DecodeState.COMPLETE && position >= this.pcmData.getFrames())
			return -1;

		if (maxFrames <= 0)
			return 0;

		AudioMetadata metadata = this.metadata;
		int bufferRemaining = buffer.remaining() / metadata.getChannels();
		if (bufferRemaining == 0)
			return 0;

		long requireFrameLong = Math.min(((long) position) + maxFrames, metadata.getTotalFrames());
		int requireFrame = (int) Math.min(requireFrameLong, Integer.MAX_VALUE);

		this.ensureData(requireFrame);

		while (true) {
			boolean success = this.newDataNotifier.await(() -> {
				DecodeState state = this.state.get();
				if (state == DecodeState.COMPLETE || state == DecodeState.FAILED)
					return true;

				int frames;
				synchronized (this.pcmDataLock) {
					frames = this.pcmData.getFrames();
				}

				return frames >= requireFrame;
			}, 1, TimeUnit.SECONDS);

			Throwable lastException = this.lastException;
			if (lastException != null)
				throw ExceptionUtils.wrap(lastException, AudioDecodeException.class, AudioDecodeException::new);

			if (success)
				break;
		}

		synchronized (this.pcmDataLock) {
			return this.pcmData.getFrame(buffer, position, maxFrames);
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

		FloatBuffer tempBuffer = this.tempBuffer;
		if (tempBuffer != null)
			tempBuffer.clear();
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
