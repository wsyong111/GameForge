package io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw;

import io.github.wsyong11.gameforge.framework.system.graphic.core.info.RenderBackedInfo;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.ex.SurfaceException;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw.opengl.GLRenderBackedInfo;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.WindowSurface;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.WindowSurfaceManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

public class GLFWWindowSurfaceManager implements WindowSurfaceManager {
	private final List<RenderBackedInfo> backendInfoList;

	public GLFWWindowSurfaceManager() {
		List<RenderBackedInfo> backendInfoList = new ArrayList<>();
		backendInfoList.add(GLRenderBackedInfo.getInstance());

		this.backendInfoList = backendInfoList
			.stream()
			.filter(Objects::nonNull)
			.toList();
	}

	@NotNull
	@Override
	public WindowSurface create() throws SurfaceException {
		return null;
	}

	@NotNull
	@Override
	public List<RenderBackedInfo> getSupportedRenderInfo() {
		return this.backendInfoList;
	}

	@Override
	public void pollEvent() {
		glfwPollEvents();
	}
}
