package io.github.wsyong11.gameforge.framework.system.window.listener;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.system.window.WindowDisplayType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2ic;

public interface WindowListener extends IListener {
	default void onResize(@NotNull Vector2ic newSize) { /* no-op */ }

	default void onMove(@NotNull Vector2ic newPosition) { /* no-op */ }

	default void onFocusChanged(boolean isFocus) { /* no-op */ }

	default void onDisplayTypeChanged(@NotNull WindowDisplayType newType) { /* no-op */ }

	default void onClose() { /* no-op */ }
}
