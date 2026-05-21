package io.github.wsyong11.gameforge.framework.math;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2ic;

public class Rect {
	public int minX;
	public int minY;
	public int maxX;
	public int maxY;

	public Rect() {
		this.minX = 0;
		this.minY = 0;
		this.maxX = 0;
		this.maxY = 0;
	}

	public Rect(int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	public Rect(@NotNull Vector2ic min, @NotNull Vector2ic max) {
		this.minX = min.x();
		this.minY = min.y();
		this.maxX = max.x();
		this.maxY = max.y();
	}
}
