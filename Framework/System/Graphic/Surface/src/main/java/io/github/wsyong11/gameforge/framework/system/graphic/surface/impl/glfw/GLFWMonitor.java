package io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw;

import io.github.wsyong11.gameforge.framework.annotation.UnsafeAPI;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.monitor.Monitor;
import org.jetbrains.annotations.NotNull;

public class GLFWMonitor implements Monitor {
	@NotNull
	public static GLFWMonitor of(long monitor) {
		GLFWMonitor instance = new GLFWMonitor(monitor);
		instance.update();
		return instance;
	}

	private final long handler;

	private String name;

	protected GLFWMonitor(long handler) {
		this.handler = handler;

		this.name = "";

	}

	@UnsafeAPI
	public long getHandler() {
		return this.handler;
	}

	public void update() {

	}
}
