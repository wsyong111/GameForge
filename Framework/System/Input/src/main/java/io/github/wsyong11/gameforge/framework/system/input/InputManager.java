package io.github.wsyong11.gameforge.framework.system.input;

import io.github.wsyong11.gameforge.framework.key.KeyCode;
import io.github.wsyong11.gameforge.framework.key.MouseButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.Set;

public interface InputManager {
	@NotNull
	@UnmodifiableView
	Set<KeyCode> getActiveKeys();

	@NotNull
	@UnmodifiableView
	Set<KeyCode> getActiveMouseButtons();

	boolean isPressed(@NotNull KeyCode code);

	boolean isPressed(@NotNull MouseButton button);

	// -------------------------------------------------------------------------------------------------------------- //

	void registerMouseListener(@NotNull MouseListener listener);

	void unregisterMouseListener(@NotNull MouseListener listener);

	void registerKeyListener(@NotNull KeyListener listener);

	void unregisterKeyListener(@NotNull KeyListener listener);
}
