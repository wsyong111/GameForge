package io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw;

import io.github.wsyong11.gameforge.framework.system.graphic.core.info.RenderBackedInfo;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.SurfaceConfig;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.ex.SurfaceException;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw.opengl.GLRenderBackedInfo;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.WindowSurface;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.WindowSurfaceManager;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.ex.WindowSurfaceBackedInitException;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.monitor.Monitor;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.concurrent.ThreadMark;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWMonitorCallback;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.hex;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindowSurfaceManager implements WindowSurfaceManager {
	private static final Logger LOGGER = Log.getLogger();

	private static final List<Supplier<RenderBackedInfo>> BACKED_INFO_PROVIDERS = List.of(
		GLRenderBackedInfo::getInstance
	);

	private final GLFWMonitorCallback monitorCallback;

	private volatile boolean inited;
	private final Object initLock;

	private ThreadMark owner;

	private final Map<Long, GLFWMonitor> monitorMap;

	private List<RenderBackedInfo> backendInfoList;

	public GLFWWindowSurfaceManager() {
		this.monitorCallback = GLFWMonitorCallback.create(this::onMonitorChanged);

		this.inited = false;
		this.initLock = new Object();

		this.owner = null;

		this.monitorMap = new ConcurrentHashMap<>();

		this.backendInfoList = List.of();
	}

	private void assertInited() {
		if (!this.inited)
			throw new IllegalStateException("Window manager is not init");
	}

	@Override
	public void init() {
		if (this.inited) return;

		synchronized (initLock) {
			if (this.inited) return;
			if (!glfwInit()) {
				throw new WindowSurfaceBackedInitException("Failed to init GLFW");
			}

			this.owner = ThreadMark.get();

			this.backendInfoList = BACKED_INFO_PROVIDERS
				.stream()
				.map(Supplier::get)
				.filter(Objects::nonNull)
				.toList();

			this.scanMonitors();
			glfwSetMonitorCallback(this.monitorCallback);

			this.inited = true;
		}
	}

	@NotNull
	@Override
	public WindowSurface create(@NotNull SurfaceConfig config) throws SurfaceException {
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
		LOGGER.debug("Scanning monitors");
		this.monitorMap.clear();

		PointerBuffer monitors = glfwGetMonitors();
		if (monitors == null)
			return;

		LOGGER.debug("Found {} monitors", monitors.limit());

		for (int i = 0; i < monitors.limit(); i++) {
			long ptr = monitors.get(i);

			GLFWMonitor instance = GLFWMonitor.of(ptr);
			LOGGER.debug("Scan monitor [{}]: {}", hex(ptr), instance);
			this.monitorMap.put(ptr, instance);
		}
	}

	private void onMonitorChanged(long monitor, int event) {
		if (event == GLFW_DISCONNECTED) {
			GLFWMonitor monitorInstance = this.monitorMap.remove(monitor);
			if (monitorInstance != null)
				LOGGER.debug("Monitor {} removed", monitorInstance);
			return;
		}

		if (event == GLFW_CONNECTED) {
			GLFWMonitor monitorInstance = this.monitorMap.get(monitor);
			if (monitorInstance != null) {
				LOGGER.warn("Monitor {} exists, nothing has been changed", monitorInstance);
				return;
			}

			GLFWMonitor instance = GLFWMonitor.of(monitor);
			this.monitorMap.put(monitor, instance);
			LOGGER.debug("Added new monitor {}", instance);

			return;
		}

		LOGGER.warn("Unknown monitor changed event: monitor={}, type={}", hex(monitor), event);
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<Monitor> getMonitors() {
		return List.copyOf(this.monitorMap.values());
	}

	@Nullable
	@Override
	public Monitor getPrimaryMonitor() {
		this.assertInited();
		this.owner.checkAssert();

		long primaryMonitor = glfwGetPrimaryMonitor();
		if (primaryMonitor == NULL)
			return null;

		return this.monitorMap.get(primaryMonitor);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public void close() {
		if (!this.inited)
			return;
		this.inited = false;
		this.owner = null;

		glfwSetMonitorCallback(null);
		this.monitorCallback.close();

		this.monitorMap.clear();

		glfwTerminate();
		this.backendInfoList = List.of();
	}
}
