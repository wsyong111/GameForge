package io.github.wsyong11.gameforge.framework.system.input.event.raw;

import io.github.wsyong11.gameforge.framework.key.KeyAction;
import io.github.wsyong11.gameforge.framework.key.KeyCode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KeyboardInputEvent extends KeyInputEvent {
	private final KeyCode keyCode;

	public KeyboardInputEvent(@NotNull KeyAction action, int mods, @NotNull KeyCode keyCode) {
		super(action, mods);

		Objects.requireNonNull(keyCode, "keyCode is null");
		this.keyCode = keyCode;
	}

	@NotNull
	public KeyCode getKeyCode() {
		return this.keyCode;
	}

	@NotNull
	@Override
	public String toString() {
		List<String> data = new ArrayList<>(7);
		this.toString(data);

		return "Keyboard[" + this.keyCode + ", " + this.getAction() + ", [" + String.join(", ", data) + "]]";
	}
}
