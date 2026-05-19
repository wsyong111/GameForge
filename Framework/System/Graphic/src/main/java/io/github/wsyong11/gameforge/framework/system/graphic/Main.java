package io.github.wsyong11.gameforge.framework.system.graphic;

import io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw.GLFWWindowSurfaceManager;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.WindowSurfaceManager;

public class Main {
	public static void main(String[] args) {
		try (WindowSurfaceManager manager = new GLFWWindowSurfaceManager()) {
			manager.init();
			System.out.println(manager.getSupportedRenderInfo());
		}
	}
}
