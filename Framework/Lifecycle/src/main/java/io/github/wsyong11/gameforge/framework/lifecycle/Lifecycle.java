package io.github.wsyong11.gameforge.framework.lifecycle;

import org.jetbrains.annotations.NotNull;

public interface Lifecycle extends ILifecycle {
	@NotNull
	static Lifecycle create() {
		return new DefaultLifecycle();
	}

	void setState(@NotNull LifecycleState newState);
}
