package io.github.wsyong11.gameforge.framework.system.window.input;

import org.jetbrains.annotations.NotNull;

public class KeyboardInputEvent extends KeyInputEvent {
	public KeyboardInputEvent(@NotNull Action action, int mods) {
		super(action, mods);
	}
}
