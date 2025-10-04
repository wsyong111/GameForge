package io.github.wsyong11.gameforge.game.core.client;

import io.github.wsyong11.gameforge.framework.key.KeyAction;
import io.github.wsyong11.gameforge.framework.key.KeyCode;
import io.github.wsyong11.gameforge.framework.key.MouseButton;
import io.github.wsyong11.gameforge.framework.system.window.listener.WindowInputListener;
import org.jetbrains.annotations.NotNull;

public class InputManagerWindowListener implements WindowInputListener {
	private final ProcessInputManager manager;

	public InputManagerWindowListener(@NotNull ProcessInputManager manager) {
		this.manager = manager;
	}

	@Override
	public void onKeyInput(@NotNull KeyCode code, int mods, @NotNull KeyAction action) {
		this.manager.processKeyInput(code, action, mods);
	}

	@Override
	public void onMouseInput(@NotNull MouseButton button, @NotNull KeyAction action) {
		this.manager.processMouseInput(button, action);
	}

	@Override
	public void onMouseMove(double x, double y) {
		this.manager.processMouseMove(x, y);
	}

	@Override
	public void onMouseFocusChanged(boolean entered) {

	}

	@Override
	public void onMouseScroll(double xDelta, double yDelta) {

	}
}
