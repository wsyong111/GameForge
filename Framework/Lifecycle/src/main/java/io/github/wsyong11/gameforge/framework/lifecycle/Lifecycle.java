package io.github.wsyong11.gameforge.framework.lifecycle;

import org.jetbrains.annotations.NotNull;

public interface Lifecycle extends ILifecycle {
	@NotNull
	static Lifecycle create() {
		return new DefaultLifecycle();
	}

	@NotNull
	static Lifecycle debug(@NotNull Lifecycle lifecycle, @NotNull String debugName) {
		return DebugLifecycle.wrap(lifecycle, debugName);
	}

	void setState(@NotNull LifecycleState newState);
}
