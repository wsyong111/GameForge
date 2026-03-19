package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio;

import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractAudio implements Audio {
	private final TaskHandler taskHandler;

	private final List<StatusCallback> callbacks;

	public AbstractAudio(@NotNull TaskHandler taskHandler) {
		Objects.requireNonNull(taskHandler, "taskHandler is null");

		this.taskHandler = taskHandler;

		this.callbacks = new ArrayList<>();
	}

	protected void invokeCallback() {
		List<StatusCallback> callbacks;
		synchronized (this.callbacks) {
			callbacks = List.copyOf(this.callbacks);
		}

		for (StatusCallback callback : callbacks)
			this.invokeCallback(callback);
	}

	protected void invokeCallback(@NotNull StatusCallback callback) {
		Objects.requireNonNull(callback, "callback is null");

		switch (this.getStatus()) {
			case READY -> this.taskHandler.run(callback::onReady);
			case FAILED -> this.taskHandler.run(callback::onFailed);
			case CLOSED -> this.taskHandler.run(callback::onClosed);
		}
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
	public void close() {
		synchronized (this.callbacks) {
			this.callbacks.clear();
		}
	}
}
