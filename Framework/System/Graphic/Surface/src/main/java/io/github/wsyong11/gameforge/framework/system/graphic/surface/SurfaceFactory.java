package io.github.wsyong11.gameforge.framework.system.graphic.surface;

import io.github.wsyong11.gameforge.framework.annotation.ThreadSensitive;
import io.github.wsyong11.gameforge.framework.system.graphic.core.info.RenderBackedInfo;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.ex.SurfaceException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SurfaceFactory {
	@NotNull
	@ThreadSensitive
	Surface create() throws SurfaceException;

	@NotNull
	List<RenderBackedInfo> getSupportedRenderInfo();
}
