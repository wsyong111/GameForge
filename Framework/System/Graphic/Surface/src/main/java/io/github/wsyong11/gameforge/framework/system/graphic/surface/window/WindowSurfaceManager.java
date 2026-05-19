package io.github.wsyong11.gameforge.framework.system.graphic.surface.window;

import io.github.wsyong11.gameforge.framework.system.graphic.surface.SurfaceFactory;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.ex.SurfaceException;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.ex.WindowSurfaceException;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.monitor.Monitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface WindowSurfaceManager extends SurfaceFactory, AutoCloseable {
	void init() throws WindowSurfaceException;

	@Override
	@NotNull
	WindowSurface create() throws SurfaceException;

	void pollEvent();

	@NotNull
	@Unmodifiable
	List<Monitor> getMonitors();

	@Override
	void close();
}
