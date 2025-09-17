package io.github.wsyong11.gameforge.framework.system.window.input;

import org.jetbrains.annotations.NotNull;

public class MouseMoveEvent implements InputEvent {
	private final double posX;
	private final double posY;

	public MouseMoveEvent(double posX, double posY) {
		this.posX = posX;
		this.posY = posY;
	}

	public double getPosX() {
		return this.posX;
	}

	public double getPosY() {
		return this.posY;
	}

	@NotNull
	@Override
	public String toString() {
		return "MouseMove[%.2f, %.2f]".formatted(this.posX, this.posY);
	}
}
