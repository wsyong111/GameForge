package io.github.wsyong11.gameforge.framework.lifecycle;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface ILifecycle {
	@NotNull
	LifecycleState getState();

	void addListener(@NotNull LifecycleListener listener);

	void removeListener(@NotNull LifecycleListener listener);

	default void assertState(@NotNull LifecycleState state) {
		Objects.requireNonNull(state, "state is null");

		LifecycleState currentState = this.getState();
		if (currentState != state)
			throw new IllegalStateException("Error lifecycle state " + currentState);
	}

	default void assertState(@NotNull LifecycleState... states) {
		Objects.requireNonNull(states, "states is null");

		LifecycleState currentState = this.getState();

		for (LifecycleState state : states) {
			if (currentState != state)
				throw new IllegalStateException("Error lifecycle state " + currentState);
		}
	}
}
