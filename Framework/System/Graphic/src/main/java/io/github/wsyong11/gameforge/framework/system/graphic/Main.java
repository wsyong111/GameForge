package io.github.wsyong11.gameforge.framework.system.graphic;

import io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw.GLFWWindowSurfaceManager;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.WindowSurfaceManager;

public class Main {
	public static void main(String[] args) {
		WindowSurfaceManager manager = new GLFWWindowSurfaceManager();
		System.out.println(manager.getSupportedRenderInfo());
	}
}
