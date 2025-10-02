package io.github.wsyong11.gameforge.framework.system.input;

import io.github.wsyong11.gameforge.framework.key.KeyAction;
import io.github.wsyong11.gameforge.framework.key.KeyCode;
import io.github.wsyong11.gameforge.framework.key.ModifyKey;
import io.github.wsyong11.gameforge.framework.key.MouseButton;
import org.jetbrains.annotations.NotNull;

public interface ProcessInputManager extends InputManagerOld {
	void processKeyInput(@NotNull KeyCode code, @NotNull KeyAction action, @ModifyKey.Mask int mods);

	void processMouseInput(@NotNull MouseButton button, @NotNull KeyAction action);

	void processMouseMove(double x, double y);
}
