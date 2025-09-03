package io.github.wsyong11.gameforge.framework.lifecycle;

import io.github.wsyong11.gameforge.framework.env.EnvConfig;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DebugLifecycle implements Lifecycle {
	private static final Logger LOGGER = Log.getLogger();

	@NotNull
	public static Lifecycle wrap(@NotNull Lifecycle delegate, @NotNull String debugName) {
		Objects.requireNonNull(delegate, "delegate is null");
		Objects.requireNonNull(debugName, "debugName is null");
		return wrap(delegate, debugName, false);
	}

	@NotNull
	public static Lifecycle wrap(@NotNull Lifecycle delegate, @NotNull String debugName, boolean ignoreConfig) {
		Objects.requireNonNull(delegate, "delegate is null");
		Objects.requireNonNull(debugName, "debugName is null");

		if (delegate instanceof DebugLifecycle debugLifecycle)
			return debugLifecycle;

		return ignoreConfig || EnvConfig.DEBUG.getValue()
			? new DebugLifecycle(delegate, debugName)
			: delegate;
	}

	private final Lifecycle delegate;
	private final String debugName;

	public DebugLifecycle(@NotNull Lifecycle delegate, @NotNull String debugName) {
		Objects.requireNonNull(delegate, "delegate is null");
		Objects.requireNonNull(debugName, "debugName is null");

		this.debugName = debugName;
		this.delegate = delegate;
	}

	@NotNull
	public String getDebugName() {
		return this.debugName;
	}

	@Override
	public void setState(@NotNull LifecycleState newState) {
		LifecycleState currentState = this.delegate.getState();
		LOGGER.debug("Lifecycle {} state change {} -> {}", this.debugName, currentState, newState);
		this.delegate.setState(newState);
	}

	@NotNull
	@Override
	public LifecycleState getState() {
		return this.delegate.getState();
	}

	@Override
	public void addListener(@NotNull LifecycleListener listener) {
		this.delegate.addListener(listener);
	}

	@Override
	public void removeListener(@NotNull LifecycleListener listener) {
		this.delegate.removeListener(listener);
	}

	@Override
	public void assertState(@NotNull LifecycleState state) {
		this.delegate.assertState(state);
	}

	@Override
	public void assertState(@NotNull LifecycleState... states) {
		this.delegate.assertState(states);
	}

	@Override
	public String toString() {
		return "DebugLifecycle[" + this.debugName + "] -> " + this.delegate;
	}
}
