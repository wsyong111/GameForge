package io.github.wsyong11.gameforge.framework.system.graphic;

import io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw.GLFWSurfaceFactory;

public class Main {
	public static void main(String[] args) {
		GLFWSurfaceFactory factory = new GLFWSurfaceFactory();
		System.out.println(factory.getSupportedRenderInfo());
	}
}
