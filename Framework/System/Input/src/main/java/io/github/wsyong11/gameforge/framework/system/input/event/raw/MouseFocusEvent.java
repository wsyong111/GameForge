package io.github.wsyong11.gameforge.framework.system.input.event.raw;

import org.jetbrains.annotations.NotNull;

public class MouseFocusEvent implements InputEvent {
	public static final MouseFocusEvent ENTER = new MouseFocusEvent(true);
	public static final MouseFocusEvent LEAVE = new MouseFocusEvent(false);

	private final boolean entered;

	public MouseFocusEvent(boolean entered) {
		this.entered = entered;
	}

	public boolean isEntered() {
		return this.entered;
	}

	@NotNull
	@Override
	public String toString() {
		return "MouseFocus[" + (this.entered ? "ENTER" : "LEAVE") + "]";
	}
}
