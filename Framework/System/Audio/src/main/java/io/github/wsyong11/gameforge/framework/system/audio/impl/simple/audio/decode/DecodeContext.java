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
import io.github.wsyong11.gameforge.util.concurrent.signal.Notifier;
import io.github.wsyong11.gameforge.util.concurrent.signal.ValueNotifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.formatTime;

public class DecodeContext implements Closeable, Comparable<DecodeContext>, Runnable {
	private static final Logger LOGGER = Log.getLogger();

	private static final int DECODE_CHUNK_SIZE_SEC = 2;
	private static final long BUFFER_SIZE_WAIT_TIMEOUT = 1000 * 5;

	private final ExecutorService pool;
	private final TaskHandler taskHandler;
	private final Identifier identifier;
	private final AudioDecoder decoder;
	private final int priority;
	private final InputStream stream;

	private final List<Audio.StatusCallback> callbacks;

	private final Notifier bufferMoreDataSignal;
	private volatile PCMList buffer;
	private volatile FloatBuffer pcmBuffer;
	private final Object bufferLock;
	private final ValueNotifier<Integer> requireBufferSizeFrame;
	private volatile DecodeState decodeState;
	private volatile AudioMetadata metadata;

	private volatile AudioStatus status;

	private volatile Future<?> activeFuture;

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

		this.bufferMoreDataSignal = new Notifier();
		this.buffer = null;
		this.pcmBuffer = null;
		this.bufferLock = new Object();
		this.requireBufferSizeFrame = new ValueNotifier<>(0);
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

	@NotNull
	public DecodeState getDecodeState() {
		return this.decodeState;
	}

	@Nullable
	public AudioDecodeException getLastDecodeException() {
		if (this.activeFuture == null || !this.activeFuture.isDone())
			return null;

		try {
			this.activeFuture.get();
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof AudioDecodeException ex)
				return ex;
			return new AudioDecodeException(cause);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		return null;
	}

	public int getBufferSize() {
		return this.buffer.getFrames();
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
		if (this.decodeState == DecodeState.FAILED || (this.activeFuture != null && !this.activeFuture.isDone()))
			return;

		this.activeFuture = this.pool.submit(this);
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

		int frameRate = this.metadata.getFrameRate();
		int channels = this.metadata.getChannels();
		this.buffer = new PCMArrayList(frameRate, channels);

		this.updateStatus(AudioStatus.READY);
		LOGGER.trace("[{}] Success to decode metadata", this.identifier);
	}

	private void doDecode() throws AudioDecodeException {
		if (this.decodeState != DecodeState.IDLE)
			return;

		assert this.metadata != null;

		int channels = this.metadata.getChannels();

		FloatBuffer tempBuf;
		if (this.pcmBuffer == null) {
			int sampleRate = this.metadata.getSampleRate();
			int bufSize = sampleRate * channels * DECODE_CHUNK_SIZE_SEC;
			tempBuf = FloatBuffer.wrap(new float[bufSize]);
			this.pcmBuffer = tempBuf;
		} else {
			tempBuf = this.pcmBuffer;
		}

		this.decodeState = DecodeState.DECODING;

		while (!Thread.currentThread().isInterrupted()) {
			LOGGER.verbose("[{}] Waiting decode request", this.identifier);
			try {
				if (!this.requireBufferSizeFrame.awaitUntil(
					(size) -> this.buffer.getFrames() < size,
					BUFFER_SIZE_WAIT_TIMEOUT,
					TimeUnit.MILLISECONDS
				)) {
					this.decodeState = DecodeState.IDLE;
					break;
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				this.decodeState = DecodeState.IDLE;
				break;
			}

			long startTime = System.nanoTime();
			LOGGER.verbose("[{}] Begin decode", this.identifier);
			tempBuf.clear();

			int len;
			try {
				len = this.decoder.decode(tempBuf, tempBuf.remaining() / channels);
			} catch (AudioDecodeException e) {
				LOGGER.error("Failed to decode pcm data", e);
				this.decodeState = DecodeState.FAILED;
				this.status = AudioStatus.FAILED;
				throw e;
			}

			if (len == -1) {
				this.decodeState = DecodeState.COMPLETE;
				this.buffer.trim();
				this.pcmBuffer = null;
				break;
			}

			tempBuf.flip();
			synchronized (this.bufferLock) {
				this.buffer.add(tempBuf);
			}

			this.bufferMoreDataSignal.signal();

			long usedTime = System.nanoTime() - startTime;
			LOGGER.verbose("[{}] Decoded {} frames, took {}", this.identifier, len, formatTime(usedTime, TimeUnit.NANOSECONDS));
		}
	}

	private void runDecode() throws AudioDecodeException {
		if (this.status == AudioStatus.FAILED)
			return;

		if (this.status == AudioStatus.LOADING)
			this.loadMetadata();

		this.doDecode();
	}

	@Override
	public void run() {
		LOGGER.trace("[{}] Start decode task", this.identifier);
		try {
			this.runDecode();
		} catch (Throwable e) {
			LOGGER.error("Exception in decoding {}", this.identifier, e);
			this.updateStatus(AudioStatus.FAILED);
			throw new CompletionException(e);
		} finally {
			LOGGER.trace("[{}] Decode task exited", this.identifier);
		}
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public void checkAudioStatus() {
		if (this.status == AudioStatus.LOADING || this.status == AudioStatus.CLOSED)
			throw new IllegalStateException("Audio metadata not decoded or audio context closed");
	}

	public void checkDecodeException() throws AudioDecodeException {
		if (this.decodeState != DecodeState.FAILED)
			return;

		AudioDecodeException exception = this.getLastDecodeException();
		if (exception == null)
			throw new AudioDecodeException("Unknown exception");

		throw exception;
	}

	public void requireData(int position) throws AudioDecodeException {
		this.checkAudioStatus();
		this.checkDecodeException();

		if (this.decodeState == DecodeState.COMPLETE)
			return;

		this.schedule();
		this.requireBufferSizeFrame.update(v -> Math.max(v, position));
	}

	public int readData(int position, @NotNull FloatBuffer buffer, int maxFrames) throws AudioDecodeException, InterruptedException {
		Objects.requireNonNull(buffer, "buffer is null");

		this.checkAudioStatus();
		this.checkDecodeException();

		if (this.decodeState == DecodeState.COMPLETE && position >= this.buffer.getFrames())
			return -1;

		if (maxFrames <= 0)
			return 0;

		int bufferRemaining = buffer.remaining() / this.metadata.getChannels();
		if (bufferRemaining == 0)
			return 0;

		int requireFrame = position + maxFrames;
		this.requireData(requireFrame);

		this.bufferMoreDataSignal.await(() ->
			this.decodeState != DecodeState.COMPLETE && this.buffer.getFrames() >= requireFrame);

		synchronized (this.bufferLock) {
			return this.buffer.getFrame(buffer, position, maxFrames);
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

		if (this.activeFuture != null) {
			this.activeFuture.cancel(true);
			try {
				this.activeFuture.get(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (ExecutionException | TimeoutException | CancellationException ignored) {
			} finally {
				this.activeFuture = null;
			}
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
