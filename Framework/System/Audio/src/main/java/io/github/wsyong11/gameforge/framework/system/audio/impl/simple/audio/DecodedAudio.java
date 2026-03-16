package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioStatus;
import io.github.wsyong11.gameforge.framework.system.audio.audio.stream.AudioStream;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class DecodedAudio implements Audio {
	private final Identifier location;
	private final TaskHandler taskHandler;

	private final List<StatusCallback> callbacks;

	@Nullable
	private volatile Provider provider;
	private volatile AudioStatus status;

	public DecodedAudio(@NotNull Identifier location, @NotNull TaskHandler taskHandler) {
		Objects.requireNonNull(location, "location is null");
		Objects.requireNonNull(taskHandler, "taskHandler is null");

		this.location = location;
		this.taskHandler = taskHandler;

		this.callbacks = new ArrayList<>();

		this.provider = null;
		this.status = AudioStatus.LOADING;
	}

	private void ensureLoadingStatus() {
		if (this.status != AudioStatus.LOADING)
			throw new IllegalStateException("Cannot set ready, need LOADING status, current status is " + this.status);
	}

	private void ensureReady() {
		if (this.status == AudioStatus.CLOSED)
			throw new IllegalStateException("This audio is closed");
		if (this.status != AudioStatus.READY)
			throw new IllegalStateException("This audio is not ready");
	}

	private void invokeCallback() {
		List<StatusCallback> callbacks;
		synchronized (this.callbacks) {
			callbacks = List.copyOf(this.callbacks);
		}

		for (StatusCallback callback : callbacks)
			this.invokeCallback(callback);
	}

	private void invokeCallback(@NotNull StatusCallback callback) {
		Objects.requireNonNull(callback, "callback is null");

		switch (this.status) {
			case READY -> this.taskHandler.run(callback::onReady);
			case FAILED -> this.taskHandler.run(callback::onFailed);
			case CLOSED -> this.taskHandler.run(callback::onClosed);
		}
	}

	public synchronized void setStatusReady(@NotNull Provider provider) {
		Objects.requireNonNull(provider, "provider is null");

		this.ensureLoadingStatus();

		this.provider = provider;
		this.status = AudioStatus.READY;

		this.invokeCallback();
	}

	public synchronized void setStatusFailed() {
		this.ensureLoadingStatus();

		this.status = AudioStatus.FAILED;

		this.invokeCallback();
	}

	@NotNull
	@Override
	public Identifier getLocation() {
		return this.location;
	}

	@NotNull
	@Override
	public AudioMetadata getMetadata() {
		this.ensureReady();

		Provider provider = this.provider;
		assert provider != null;
		return provider.getMetadata();
	}

	@NotNull
	@Override
	public AudioStatus getStatus() {
		return this.status;
	}

	@NotNull
	@Override
	public AudioStream newStream() {
		this.ensureReady();
		Provider provider = this.provider;
		assert provider != null;
		return provider.newStream();
	}

	@NotNull
	@Override
	public AudioStream openStreamingStream() {
		this.ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void registerStatusCallback(@NotNull StatusCallback callback) {
		Objects.requireNonNull(callback, "callback is null");

		synchronized (this.callbacks) {
			this.callbacks.add(callback);
		}

		this.invokeCallback(callback);
	}

	@Override
	public void unregisterStatusCallback(@NotNull StatusCallback callback) {
		Objects.requireNonNull(callback, "callback is null");

		synchronized (this.callbacks) {
			this.callbacks.remove(callback);
		}
	}

	@Override
	public synchronized void close() {
		if (this.status == AudioStatus.CLOSED)
			return;
		this.status = AudioStatus.CLOSED;

		Provider provider = this.provider;
		if (provider != null)
			provider.close();

		this.invokeCallback();

		this.callbacks.clear();
	}

	public interface Provider {
		@NotNull
		AudioMetadata getMetadata();

		@NotNull
		AudioStream newStream();

		void close();
	}
}
