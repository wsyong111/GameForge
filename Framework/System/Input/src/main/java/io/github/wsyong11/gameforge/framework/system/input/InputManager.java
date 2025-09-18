package io.github.wsyong11.gameforge.framework.system.input;

import io.github.wsyong11.gameforge.framework.key.KeyAction;
import io.github.wsyong11.gameforge.framework.key.KeyCode;
import io.github.wsyong11.gameforge.framework.key.MouseButton;
import io.github.wsyong11.gameforge.framework.system.input.listener.MouseListener;
import io.github.wsyong11.gameforge.framework.system.input.listener.KeyListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector2dc;

import java.util.Set;

public interface InputManager {
	@NotNull
	@UnmodifiableView
	Set<KeyCode> getActiveKeys();

	boolean isPressed(@NotNull KeyCode code);

	@NotNull
	KeyAction getKeyAction(@NotNull KeyCode code);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@UnmodifiableView
	Set<MouseButton> getActiveMouseButtons();

	boolean isPressed(@NotNull MouseButton button);

	@NotNull
	KeyAction getMouseAction(@NotNull MouseButton button);

	@NotNull
	Vector2dc getMousePosition();

	// -------------------------------------------------------------------------------------------------------------- //

	void registerMouseListener(@NotNull MouseListener listener);

	void unregisterMouseListener(@NotNull MouseListener listener);

	void registerKeyListener(@NotNull KeyListener listener);

	void unregisterKeyListener(@NotNull KeyListener listener);
}
