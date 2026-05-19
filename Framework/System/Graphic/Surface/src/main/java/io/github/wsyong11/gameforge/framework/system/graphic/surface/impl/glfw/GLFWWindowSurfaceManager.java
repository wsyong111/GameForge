package io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw;

import io.github.wsyong11.gameforge.framework.system.graphic.core.info.RenderBackedInfo;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.ex.SurfaceException;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw.opengl.GLRenderBackedInfo;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.WindowSurface;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.WindowSurfaceManager;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.ex.WindowSurfaceBackedInitException;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.monitor.Monitor;
import io.github.wsyong11.gameforge.util.concurrent.ThreadMark;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.lwjgl.glfw.GLFWMonitorCallback;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class GLFWWindowSurfaceManager implements WindowSurfaceManager {
	private final GLFWMonitorCallback monitorCallback;

	private boolean inited;
	private ThreadMark owner;

	private final Map<Long, GLFWMonitor> monitorMap;

	private List<RenderBackedInfo> backendInfoList;

	public GLFWWindowSurfaceManager() {
		this.monitorCallback = GLFWMonitorCallback.create(this::onMonitorChanged);

		this.inited = false;
		this.owner = null;

		this.monitorMap = new HashMap<>();

		this.backendInfoList = List.of();
	}

	private void assertInited() {
		if (!this.inited)
			throw new IllegalStateException("Window manager is not init");
	}

	@Override
	public void init() {
		if (this.inited)
			return;
		this.inited = true;

		if (!glfwInit()) {
			this.inited = false;
			throw new WindowSurfaceBackedInitException("Failed to init GLFW");
		}

		this.owner = ThreadMark.get();

		List<RenderBackedInfo> backendInfoList = new ArrayList<>();
		backendInfoList.add(GLRenderBackedInfo.getInstance());

		this.backendInfoList = backendInfoList
			.stream()
			.filter(Objects::nonNull)
			.toList();

		this.monitorMap.clear();
		this.scanMonitors();
		glfwSetMonitorCallback(this.monitorCallback);
	}

	@NotNull
	@Override
	public WindowSurface create() throws SurfaceException {
		this.assertInited();
		this.owner.checkAssert();
		return null;
	}

	@NotNull
	@Override
	public List<RenderBackedInfo> getSupportedRenderInfo() {
		return this.backendInfoList;
	}

	@Override
	public void pollEvent() {
		this.assertInited();
		this.owner.checkAssert();
		glfwPollEvents();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private void scanMonitors() {

	}

	private void onMonitorChanged(long monitor, int event) {

	}

	@NotNull
	@Unmodifiable
	@Override
	public List<Monitor> getMonitors() {
		return List.copyOf(this.monitorMap.values());
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public void close() {
		if (!this.inited)
			return;
		this.inited = false;

		glfwSetMonitorCallback(null);
		this.monitorCallback.close();

		this.monitorMap.clear();

		glfwTerminate();
		this.backendInfoList = List.of();
	}
}
