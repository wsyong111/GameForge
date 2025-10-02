package io.github.wsyong11.gameforge.framework.system.input;

import io.github.wsyong11.gameforge.framework.key.InputKey;
import io.github.wsyong11.gameforge.framework.key.KeyAction;
import io.github.wsyong11.gameforge.framework.key.KeyCode;
import io.github.wsyong11.gameforge.framework.key.MouseButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Set;

public interface KeyMap {
	boolean isPressed(@NotNull InputKey key);

	@NotNull
	KeyAction getAction(@NotNull InputKey key);

	@NotNull
	@Unmodifiable
	Set<InputKey> getPressedKeys();
}
