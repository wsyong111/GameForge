package io.github.wsyong11.gameforge.framework.key;

public enum MouseButton {
	UNKNOWN(-1),
	LEFT(0),
	MIDDLE(1),
	RIGHT(2),
	BUTTON_4(4),
	BUTTON_5(5),
	BUTTON_6(6),
	BUTTON_7(7),
	BUTTON_8(8),
	BUTTON_9(9);

	private final int index;

	MouseButton(int index) {
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}
}
