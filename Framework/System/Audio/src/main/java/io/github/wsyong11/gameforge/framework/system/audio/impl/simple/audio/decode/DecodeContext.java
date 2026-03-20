package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio.decode;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioStatus;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import io.github.wsyong11.gameforge.framework.system.audio.audio.pcm.PCMArrayList;
import io.github.wsyong11.gameforge.framework.system.audio.audio.pcm.PCMList;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class DecodeContext implements Closeable, Comparable<DecodeContext>, Runnable {
	private static final Logger LOGGER = Log.getLogger();

	private static final int DECODE_CHUNK_SIZE_SEC = 2;
	private static final long BUFFER_SIZE_WAIT_TIMEOUT = 1000 * 10;

	private final ExecutorService pool;
	private final TaskHandler taskHandler;
	private final Identifier identifier;
	private final AudioDecoder decoder;
	private final int priority;
	private final InputStream stream;

	private final List<Audio.StatusCallback> callbacks;

	private final Object bufferMoreDataSignal;
	private final Object requireBufferSizeUpdateSignal;
	private volatile PCMList buffer;
	private volatile FloatBuffer pcmBuffer;
	private volatile int requireBufferSizeFrame;
	private volatile DecodeState decodeState;
	private volatile AudioMetadata metadata;

	private volatile AudioStatus status;

	private volatile CompletableFuture<?> activeFuture;

	public DecodeContext(
		@NotNull ExecutorService pool,
		@NotNull TaskHandler taskHandler,
		@NotNull Identifier identifier,
		@NotNull AudioDecoder decoder,
		int priority,
		@NotNull InputStream stream
	) {
		Objects.requireNonNull(pool, "pool is null");
		Objects.requireNonNull(taskHandler, "taskHandler is null");
		Objects.requireNonNull(identifier, "identifier is null");
		Objects.requireNonNull(decoder, "decoder is null");
		Objects.requireNonNull(stream, "stream is null");

		this.pool = pool;
		this.taskHandler = taskHandler;
		this.identifier = identifier;
		this.decoder = decoder;
		this.priority = priority;
		this.stream = stream;

		this.callbacks = new ArrayList<>();

		this.bufferMoreDataSignal = new Object();
		this.requireBufferSizeUpdateSignal = new Object();
		this.buffer = null;
		this.pcmBuffer = null;
		this.requireBufferSizeFrame = 0;
		this.decodeState = DecodeState.IDLE;
		this.metadata = null;

		this.status = AudioStatus.LOADING;

		this.activeFuture = null;
	}

	@NotNull
	public Identifier getIdentifier() {
		return this.identifier;
	}

	public int getPriority() {
		return this.priority;
	}

	@NotNull
	public AudioDecoder getDecoder() {
		return this.decoder;
	}

	@NotNull
	public AudioStatus getStatus() {
		return this.status;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public AudioMetadata getMetadata() {
		if (this.status != AudioStatus.READY)
			throw new IllegalStateException("Cannot get audio metadata, current status is " + this.status);

		AudioMetadata metadata = this.metadata;
		assert metadata != null;
		return metadata;
	}

	protected void updateStatus(@NotNull AudioStatus newStatus) {
		Objects.requireNonNull(newStatus, "newStatus is null");

		this.status = newStatus;
		this.invokeCallback(newStatus);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private synchronized void schedule() {
		if (this.activeFuture != null && !this.activeFuture.isDone() && !this.activeFuture.isCompletedExceptionally())
			return;

		this.activeFuture = CompletableFuture.runAsync(this, this.pool);
	}

	public void preload() {
		this.schedule();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	protected AudioMetadata parseMetadata() throws AudioDecodeException {
		return this.decoder.getMetadata();
	}

	private void loadMetadata() {
		if (this.status != AudioStatus.LOADING)
			return;

		try {
			this.metadata = this.parseMetadata();
		} catch (Throwable e) {
			this.updateStatus(AudioStatus.FAILED);
			LOGGER.error("Failed to parse metadata from audio {}", this.identifier, e);
			return;
		}

		int sampleRate = this.metadata.getFrameRate();
		int channels = this.metadata.getChannels();
		this.buffer = new PCMArrayList(sampleRate, channels);

		this.updateStatus(AudioStatus.READY);
	}

	private void doDecode() {
		if (this.decodeState != DecodeState.IDLE)
			return;

		assert this.metadata != null;

		FloatBuffer tempBuf;
		if (this.pcmBuffer == null) {
			int channels = this.metadata.getChannels();
			int sampleRate = this.metadata.getSampleRate();
			int bufSize = sampleRate * channels * DECODE_CHUNK_SIZE_SEC;
			tempBuf = FloatBuffer.wrap(new float[bufSize]);
			this.pcmBuffer = tempBuf;
		} else {
			tempBuf = this.pcmBuffer;
		}

		this.decodeState = DecodeState.DECODING;

		while (!Thread.currentThread().isInterrupted()) {
			synchronized (this.requireBufferSizeUpdateSignal) {
				while (this.buffer.getFrames() >= this.requireBufferSizeFrame) {
					try {
						if (!this.requireBufferSizeUpdateSignal.wait(BUFFER_SIZE_WAIT_TIMEOUT)) {
							// 超时后没人需要更多数据，退出循环
							break;
						}
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			}

			tempBuf.clear();

			int len;
			try {
				len = this.decoder.decode(tempBuf, tempBuf.remaining());
			} catch (AudioDecodeException e) {
				LOGGER.error("Failed to decode pcm data", e);
				this.decodeState = DecodeState.FAILED;
				this.status = AudioStatus.FAILED;
				break;
			}

			if (len == -1) {
				this.decodeState = DecodeState.COMPLETE;
				break;
			}

			tempBuf.flip();
			this.buffer.add(tempBuf);

			synchronized (this.bufferMoreDataSignal) {
				this.bufferMoreDataSignal.notifyAll();
			}
		}
	}

	private void runDecode() {
		if (this.status == AudioStatus.FAILED)
			return;

		if (this.status == AudioStatus.LOADING)
			this.loadMetadata();

		this.doDecode();
	}

	@Override
	public void run() {
		try {
			this.runDecode();
		} catch (Throwable e) {
			this.updateStatus(AudioStatus.FAILED);
			throw e;
		}
	}

	// -------------------------------------------------------------------------------------------------------------- //

	protected void invokeCallback(@NotNull AudioStatus status) {
		List<Audio.StatusCallback> callbacks;
		synchronized (this.callbacks) {
			callbacks = List.copyOf(this.callbacks);
		}

		for (Audio.StatusCallback callback : callbacks)
			this.invokeCallback(status, callback);
	}

	protected void invokeCallback(@NotNull AudioStatus status, @NotNull Audio.StatusCallback callback) {
		Objects.requireNonNull(callback, "callback is null");

		switch (status) {
			case READY -> this.taskHandler.run(callback::onReady);
			case FAILED -> this.taskHandler.run(callback::onFailed);
			case CLOSED -> this.taskHandler.run(callback::onClosed);
		}
	}

	public void registerStatusCallback(@NotNull Audio.StatusCallback callback) {
		Objects.requireNonNull(callback, "callback is null");

		synchronized (this.callbacks) {
			this.callbacks.add(callback);
		}

		this.invokeCallback(this.status);
	}

	public void unregisterStatusCallback(@NotNull Audio.StatusCallback callback) {
		Objects.requireNonNull(callback, "callback is null");

		synchronized (this.callbacks) {
			this.callbacks.remove(callback);
		}
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public int compareTo(@NotNull DecodeContext o) {
		Objects.requireNonNull(o, "o is null");
		return Integer.compare(this.getPriority(), o.getPriority());
	}

	@Override
	public void close() {
		if (this.status == AudioStatus.CLOSED)
			return;
		this.updateStatus(AudioStatus.CLOSED);

		this.activeFuture.cancel(true);
		try {
			this.activeFuture.get(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException | TimeoutException ignored) {
		} finally {
			this.activeFuture = null;
		}

		try {
			this.decoder.close();
		} catch (IOException e) {
			LOGGER.error("Exception when closing decoder", e);
		} finally {
			try {
				this.stream.close();
			} catch (IOException e) {
				LOGGER.error("Exception when closing audio data stream", e);
			}
			this.metadata = null;
			this.buffer = null;
		}
	}

	public enum DecodeState {
		IDLE,
		DECODING,
		COMPLETE,
		FAILED
	}
}
