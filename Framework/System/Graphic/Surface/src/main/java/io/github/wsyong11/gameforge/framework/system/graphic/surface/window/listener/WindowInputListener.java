package io.github.wsyong11.gameforge.framework.system.graphic.surface.window.listener;

import io.github.wsyong11.gameforge.framework.key.KeyAction;
import io.github.wsyong11.gameforge.framework.key.KeyCode;
import io.github.wsyong11.gameforge.framework.key.ModifyKey;
import io.github.wsyong11.gameforge.framework.key.MouseButton;
import io.github.wsyong11.gameforge.framework.listener.IListener;
import org.jetbrains.annotations.NotNull;

public interface WindowInputListener extends IListener {
	default void onKeyInput(@NotNull KeyCode code, @ModifyKey.Mask int mods, @NotNull KeyAction action) { /* no-op */ }

	default void onMouseInput(@NotNull MouseButton button, @NotNull KeyAction action) { /* no-op */ }

	default void onMouseMove(double x, double y) { /* no-op */ }

	default void onMouseFocusChanged(boolean entered) { /* no-op */ }

	default void onMouseScroll(double xDelta, double yDelta) { /* no-op */ }
}
