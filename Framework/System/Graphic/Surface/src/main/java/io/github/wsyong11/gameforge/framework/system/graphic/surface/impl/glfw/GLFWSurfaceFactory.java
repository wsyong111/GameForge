package io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw;

import io.github.wsyong11.gameforge.framework.system.graphic.core.info.RenderBackendInfo;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.Surface;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.SurfaceFactory;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.ex.SurfaceException;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw.opengl.GLRenderBackendInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GLFWSurfaceFactory implements SurfaceFactory {
	private final List<RenderBackendInfo> backendInfoList;

	public GLFWSurfaceFactory() {
		List<RenderBackendInfo> backendInfoList = new ArrayList<>();
		backendInfoList.add(GLRenderBackendInfo.getInstance());

		this.backendInfoList = backendInfoList
			.stream()
			.filter(Objects::nonNull)
			.toList();
	}

	@NotNull
	@Override
	public Surface create() throws SurfaceException {
		return null;
	}

	@NotNull
	@Override
	public List<RenderBackendInfo> getSupportedRenderInfo() {
		return this.backendInfoList;
	}
}
