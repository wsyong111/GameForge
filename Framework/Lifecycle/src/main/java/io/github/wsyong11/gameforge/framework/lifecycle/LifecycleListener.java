package io.github.wsyong11.gameforge.framework.lifecycle;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface LifecycleListener extends IListener {
	void onStateUpdate(@NotNull LifecycleState newState);
}
