package io.github.wsyong11.gameforge.framework.system.input.event.raw;

import io.github.wsyong11.gameforge.framework.key.KeyAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MouseClickEvent extends KeyInputEvent {
	private final Key key;

	public MouseClickEvent(@NotNull KeyAction action, int mods, @NotNull Key key) {
		super(action, mods);

		Objects.requireNonNull(key, "key is null");
		this.key = key;
	}

	@NotNull
	public Key getKey() {
		return this.key;
	}

	@NotNull
	@Override
	public String toString() {
		List<String> data = new ArrayList<>(7);
		this.toString(data);
		return "MouseClick[" + this.key + ", " + this.getAction() + ", [" + String.join(", ", data) + "]]";
	}

	public enum Key {
		LEFT,
		MIDDLE,
		RIGHT
	}
}
