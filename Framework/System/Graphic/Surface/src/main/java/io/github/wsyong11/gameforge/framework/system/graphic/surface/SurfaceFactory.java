package io.github.wsyong11.gameforge.framework.system.graphic.surface;

import io.github.wsyong11.gameforge.framework.system.graphic.core.info.RenderBackendInfo;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.ex.SurfaceException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SurfaceFactory {
	@NotNull
	Surface create() throws SurfaceException;

	@NotNull
	List<RenderBackendInfo> getSupportedRenderInfo();
}
