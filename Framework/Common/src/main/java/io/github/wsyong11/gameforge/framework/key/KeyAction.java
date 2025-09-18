package io.github.wsyong11.gameforge.framework.key;

public enum KeyAction {
	UP,
	DOWN,
	HOLD;

	public boolean isPressed() {
		return this == DOWN || this == HOLD;
	}
}
