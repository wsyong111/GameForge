package io.github.wsyong11.gameforge.framework.lifecycle;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class LifecycleInstance implements LifecycleProvider {
	protected final Lifecycle lifecycle;

	protected LifecycleInstance() {
		this(new DefaultLifecycle());
	}

	protected LifecycleInstance(@NotNull String debugName) {
		this(DebugLifecycle.wrap(new DefaultLifecycle(), debugName));
	}

	protected LifecycleInstance(@NotNull Lifecycle lifecycle) {
		Objects.requireNonNull(lifecycle, "lifecycle is null");
		this.lifecycle = lifecycle;
	}

	protected abstract void onStarting() throws Throwable;

	protected abstract void onRunning() throws Throwable;

	protected abstract void onStopping() throws Throwable;

	protected abstract void onDestroy();

	protected abstract void onError(@NotNull Throwable exception);

	@NotNull
	@Override
	public ILifecycle getLifecycle() {
		return this.lifecycle;
	}

	public void start() {
		this.lifecycle.assertState(LifecycleState.CREATED);

		try {
			this.lifecycle.setState(LifecycleState.STARTING);
			this.onStarting();

			this.lifecycle.setState(LifecycleState.RUNNING);
			this.onRunning();
		} catch (Throwable e) {
			this.processError(e);
		}
	}

	private void processError(@NotNull Throwable exception) {
		try {
			this.onError(exception);
		} finally {
			this.lifecycle.setState(LifecycleState.ERROR);
		}
	}

	public void stop() {
		this.lifecycle.assertState(LifecycleState.RUNNING);

		try {
			this.lifecycle.setState(LifecycleState.STOPPING);
			this.onStopping();

			// Destroy may go exception
			this.onDestroy();
			this.lifecycle.setState(LifecycleState.DESTROYED);
		} catch (Throwable e) {
			this.processError(e);
		}
	}
}
