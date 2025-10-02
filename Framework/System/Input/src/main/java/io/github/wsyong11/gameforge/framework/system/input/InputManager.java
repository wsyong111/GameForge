package io.github.wsyong11.gameforge.framework.system.input;

import io.github.wsyong11.gameforge.framework.system.input.listener.KeyListener;
import io.github.wsyong11.gameforge.framework.system.input.listener.MouseListener;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2dc;

public interface InputManager {
	@NotNull
	KeyMap getKeyMap();

	@NotNull
	Vector2dc getMousePosition();

	void registerMouseListener(@NotNull MouseListener listener);

	void unregisterMouseListener(@NotNull MouseListener listener);

	void registerKeyListener(@NotNull KeyListener listener);

	void unregisterKeyListener(@NotNull KeyListener listener);
}
