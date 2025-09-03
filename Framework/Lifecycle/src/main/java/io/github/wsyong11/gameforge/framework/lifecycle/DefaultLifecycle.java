package io.github.wsyong11.gameforge.framework.lifecycle;

import org.jetbrains.annotations.NotNull;

public class DefaultLifecycle extends AbstractLifecycle {
	@Override
	protected boolean canTransitionTo(@NotNull LifecycleState currentState, @NotNull LifecycleState newState) {
		// CREATING -> STARTING
		// STARTING -> RUNNING
		// STARTING -> STOPPING
		// RUNNING -> STOPPING
		// STOPPING -> DESTROYED
		return (newState == LifecycleState.ERROR && currentState != LifecycleState.DESTROYED)
			|| (currentState == LifecycleState.CREATED && newState == LifecycleState.STARTING)
			|| (currentState == LifecycleState.STARTING && newState == LifecycleState.RUNNING)
			|| (currentState == LifecycleState.STARTING && newState == LifecycleState.STOPPING)
			|| (currentState == LifecycleState.RUNNING && newState == LifecycleState.STOPPING)
			|| (currentState == LifecycleState.STOPPING && newState == LifecycleState.DESTROYED);
	}
}
