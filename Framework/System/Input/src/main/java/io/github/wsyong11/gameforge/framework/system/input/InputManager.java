package io.github.wsyong11.gameforge.framework.system.input;

import io.github.wsyong11.gameforge.framework.key.InputKey;
import io.github.wsyong11.gameforge.framework.tick.Tickable;
import org.jetbrains.annotations.NotNull;

public interface InputManager extends IInputManager, Tickable {
	void processInput(@NotNull InputKey key, boolean pressed);
}
