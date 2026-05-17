package io.github.wsyong11.gameforge.framework.system.graphic.surface;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public interface Surface extends AutoCloseable {
	@NotNull
	Vector2ic getSize();

	void getSize(@NotNull Vector2i dest);

	boolean isValid();

	void present();

	@Override
	void close();
}
