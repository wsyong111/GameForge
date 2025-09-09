package io.github.wsyong11.gameforge.framework.lifecycle;

import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractLifecycle implements Lifecycle {
	private static final Logger LOGGER = Log.getLogger();

	private final ListenerList listeners;

	private LifecycleState state;

	public AbstractLifecycle() {
		this.listeners = ListenerList.sync();

		this.state = LifecycleState.CREATED;
	}

	@NotNull
	@Override
	public LifecycleState getState() {
		return this.state;
	}

	@Override
	public void setState(@NotNull LifecycleState newState) {
		Objects.requireNonNull(newState, "newState is null");

		if (this.state == newState)
			return;

		if (!this.canTransitionTo(this.state, newState))
			throw new IllegalStateException("Cannot transition state " + this.state + " to " + newState);

		this.state = newState;
		this.onEnterState(newState);
	}

	@Override
	public void addListener(@NotNull LifecycleListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listeners.add(LifecycleListener.class, listener);
	}

	@Override
	public void removeListener(@NotNull LifecycleListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listeners.remove(LifecycleListener.class, listener);
	}

	protected void onEnterState(@NotNull LifecycleState state) {
		this.listeners.fire(
			LifecycleListener.class,
			l -> l.onStateUpdate(state),
			ListenerExceptionCallback.log(LOGGER));
	}

	protected boolean canTransitionTo(@NotNull LifecycleState currentState, @NotNull LifecycleState newState) {
		return true;
	}

	@Override
	public String toString() {
		return "Lifecycle[" + this.state + "]";
	}
}
