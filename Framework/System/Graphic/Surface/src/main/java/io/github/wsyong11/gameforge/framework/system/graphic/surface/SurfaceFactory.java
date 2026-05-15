package io.github.wsyong11.gameforge.framework.system.graphic.surface;

import io.github.wsyong11.gameforge.framework.system.graphic.core.capacity.GraphicCapacity;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.ex.SurfaceException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Set;

public interface SurfaceFactory {
	@NotNull
	Surface create() throws SurfaceException;

	@NotNull
	@UnmodifiableView
	Set<GraphicCapacity> getSupportedCapabilities();
}
