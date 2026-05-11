package io.github.wsyong11.gameforge.framework.system.graphic.surface;

import io.github.wsyong11.gameforge.framework.system.graphic.capacity.GraphicCapacity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Set;

public interface SurfaceManager {
	@NotNull
	Surface createSurface();

	@NotNull
	@UnmodifiableView
	Set<GraphicCapacity> getSupportedCapabilities();
}
