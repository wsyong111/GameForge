package io.github.wsyong11.gameforge.framework.system.graphic.surface.window;

import io.github.wsyong11.gameforge.framework.system.graphic.surface.SurfaceFactory;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.ex.SurfaceException;
import org.jetbrains.annotations.NotNull;

public interface WindowSurfaceManager extends SurfaceFactory {
	@Override
	@NotNull
	WindowSurface create() throws SurfaceException;

	void pollEvent();
}
