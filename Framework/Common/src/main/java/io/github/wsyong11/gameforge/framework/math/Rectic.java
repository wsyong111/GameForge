package io.github.wsyong11.gameforge.framework.math;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public interface Rectic {
	int minX();

	int minY();

	int maxX();

	int maxY();

	int width();

	int height();

	int centerX();

	int centerY();

	boolean isEmpty();

	int left();

	int right();

	int top();

	int bottom();

	@Contract("_ -> param1")
	@NotNull
	Vector2i min(@NotNull Vector2i dest);

	@Contract("_ -> param1")
	@NotNull
	Vector2i max(@NotNull Vector2i dest);

	boolean contains(int x, int y);

	boolean contains(@NotNull Vector2ic pos);

	boolean contains(@NotNull Recti other);

	int distanceTo(int x, int y);

	int distanceTo(@NotNull Vector2ic pos);

	int area();
}
