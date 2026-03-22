package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio.decode;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioStatus;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.concurrent.FutureUtils;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import io.github.wsyong11.gameforge.util.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DecodeContext implements Closeable, Comparable<DecodeContext> {
	private static final Logger LOGGER = Log.getLogger();

	private static final int DECODE_CHUNK_SIZE_SEC = 2;
	private static final long BUFFER_SIZE_WAIT_TIMEOUT = 1000 * 5;

	private final ExecutorService pool;
	private final TaskHandler taskHandler;
	private final Identifier id;
	private final AudioDecoder decoder;
	private final int priority;
	private final InputStream stream;

	private volatile AudioMetadata metadata;
	private volatile Future<?> decodeMetadataFuture;
	private volatile DecodeTask decodeTask;
	private volatile Future<?> lastDecodeFuture;

	private final AtomicReference<AudioStatus> status;
	private final List<Audio.StatusCallback> callbacks;

	public DecodeContext(
		@NotNull ExecutorService pool,
		@NotNull TaskHandler taskHandler,
		@NotNull Identifier id,
		@NotNull AudioDecoder decoder,
		int priority,
		@NotNull InputStream stream
	) {
		Objects.requireNonNull(pool, "pool is null");
		Objects.requireNonNull(taskHandler, "taskHandler is null");
		Objects.requireNonNull(id, "id is null");
		Objects.requireNonNull(decoder, "decoder is null");
		Objects.requireNonNull(stream, "stream is null");

		this.pool = pool;
		this.taskHandler = taskHandler;
		this.id = id;
		this.decoder = decoder;
		this.priority = priority;
		this.stream = stream;

		this.metadata = null;
		this.decodeMetadataFuture = null;
		this.decodeTask = null;
		this.lastDecodeFuture = null;

		this.callbacks = new ArrayList<>();
		this.status = new AtomicReference<>(AudioStatus.LOADING);
	}

	@NotNull
	public Identifier getId() {
		return this.id;
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
		return this.status.get();
	}

	@Nullable
	public DecodeTask.DecodeState getDecodeState() {
		DecodeTask decodeTask = this.decodeTask;
		return decodeTask == null ? null : decodeTask.getState();
	}

	@Nullable
	public AudioDecodeException getLastDecodeException() {
		DecodeTask decodeTask = this.decodeTask;
		Future<?> decodeMetadataFuture = this.decodeMetadataFuture;

		if (decodeMetadataFuture != null) {
			Throwable exception = FutureUtils.getException(decodeMetadataFuture);
			if (exception != null)
				return ExceptionUtils.wrap(exception, AudioDecodeException.class, AudioDecodeException::new);
		}

		if (decodeTask != null) {
			Throwable exception = decodeTask.getLastException();
			if (exception != null)
				return ExceptionUtils.wrap(exception, AudioDecodeException.class, AudioDecodeException::new);
		}

		return null;
	}

	public int getBufferSize() {
		DecodeTask decodeTask = this.decodeTask;
		return decodeTask == null ? 0 : decodeTask.getBufferSize();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public AudioMetadata getMetadata() {
		if (this.status.get() != AudioStatus.READY)
			throw new IllegalStateException("Cannot get audio metadata, metadata decode failed or not ready");

		return this.metadata;
	}

	protected void updateStatus(@NotNull AudioStatus newStatus) {
		Objects.requireNonNull(newStatus, "newStatus is null");

		if (this.status.getAndSet(newStatus) == newStatus)
			return;

		this.invokeCallback(newStatus);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private synchronized void schedule() {
		AudioStatus status = this.status.get();
		if (status == AudioStatus.READY) {
			if (this.decodeTask.getState() == DecodeTask.DecodeState.IDLE)
				this.lastDecodeFuture = this.pool.submit(this.decodeTask);
		} else if (status == AudioStatus.LOADING) {
			if (this.decodeMetadataFuture == null)
				this.decodeMetadataFuture = this.pool.submit(this::loadMetadata);
		}
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
		if (this.status.get() != AudioStatus.LOADING)
			return;

		try {
			this.metadata = this.parseMetadata();
		} catch (Throwable e) {
			this.updateStatus(AudioStatus.FAILED);
			LOGGER.error("Failed to parse metadata from audio {}", this.id, e);
			return;
		}

		LOGGER.trace("[{}] Success to decode metadata", this.id);

		this.decodeTask = new DecodeTask(
			this.id,
			this.metadata,
			this.decoder,
			2,
			10,
			TimeUnit.SECONDS
		);
		this.updateStatus(AudioStatus.READY);

		this.schedule();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public void checkDecodeException() throws AudioDecodeException {
		AudioDecodeException exception = this.getLastDecodeException();
		if (exception != null)
			throw exception;
	}

	public int readData(int position, @NotNull FloatBuffer buffer, int maxFrames) throws AudioDecodeException, InterruptedException {
		Objects.requireNonNull(buffer, "buffer is null");

		this.checkDecodeException();

		DecodeTask decodeTask = this.decodeTask;
		if (decodeTask == null)
			throw new IllegalStateException("Decoder doesn't ready");

		return decodeTask.readData(position, buffer, maxFrames);
	}

//	public void checkDecodeException() throws AudioDecodeException {
//		if (this.decodeState != DecodeState.FAILED)
//			return;
//
//		AudioDecodeException exception = this.getLastDecodeException();
//		if (exception == null)
//			throw new AudioDecodeException("Unknown exception");
//
//		throw exception;
//	}
//
//	public void requireData(int position) throws AudioDecodeException {
//		this.checkAudioStatus();
//		this.checkDecodeException();
//
//		if (this.decodeState == DecodeState.COMPLETE)
//			return;
//
//		this.schedule();
//		this.requireBufferSizeFrame.update(v -> Math.max(v, position));
//	}
//
//	public int readData(int position, @NotNull FloatBuffer buffer, int maxFrames) throws AudioDecodeException, InterruptedException {
//		Objects.requireNonNull(buffer, "buffer is null");
//
//		this.checkAudioStatus();
//		this.checkDecodeException();
//
//		if (this.decodeState == DecodeState.COMPLETE && position >= this.buffer.getFrames())
//			return -1;
//
//		if (maxFrames <= 0)
//			return 0;
//
//		int bufferRemaining = buffer.remaining() / this.metadata.getChannels();
//		if (bufferRemaining == 0)
//			return 0;
//
//		int requireFrame = position + maxFrames;
//		this.requireData(requireFrame);
//
//		this.bufferMoreDataSignal.await(() ->
//			this.decodeState != DecodeState.COMPLETE && this.buffer.getFrames() >= requireFrame);
//
//		synchronized (this.bufferLock) {
//			return this.buffer.getFrame(buffer, position, maxFrames);
//		}
//	}

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

		this.invokeCallback(this.status.get(), callback);
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
		if (this.status.getAndSet(AudioStatus.CLOSED) == AudioStatus.CLOSED)
			return;

		FutureUtils.cancelAwait(this.decodeMetadataFuture, 5, TimeUnit.SECONDS);
		this.decodeMetadataFuture = null;

		FutureUtils.cancelAwait(this.lastDecodeFuture, 5, TimeUnit.SECONDS);
		this.lastDecodeFuture = null;

		DecodeTask decodeTask = this.decodeTask;
		if (decodeTask != null) {
			decodeTask.close();
			this.decodeTask = null;
		}

		try {
			this.decoder.close();
		} catch (IOException e) {
			LOGGER.error("Exception when closing decoder", e);
		}

		try {
			this.stream.close();
		} catch (IOException e) {
			LOGGER.error("Exception when closing audio data stream", e);
		}

		this.metadata = null;
	}
}
