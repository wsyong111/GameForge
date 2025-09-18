package io.github.wsyong11.gameforge.framework.system.window.listener;

import io.github.wsyong11.gameforge.framework.key.KeyCode;
import io.github.wsyong11.gameforge.framework.key.ModifyKey;
import io.github.wsyong11.gameforge.framework.key.MouseButton;
import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.system.input.event.raw.InputEvent;
import io.github.wsyong11.gameforge.framework.key.KeyAction;
import org.jetbrains.annotations.NotNull;

public interface WindowInputListener extends IListener {
	void onKeyInput(@NotNull KeyCode code, @ModifyKey.Mask int mods, @NotNull KeyAction action);

	void onMouseInput(@NotNull MouseButton button, @NotNull KeyAction action);

	void onMouseMove(double x, double y);

	void onMouseFocusChanged(boolean entered);

	void onMouseScroll(double xDelta, double yDelta);
}
