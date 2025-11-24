package io.github.wsyong11.gameforge.framework.system.window.impl.glfw.graphic;

import io.github.wsyong11.gameforge.framework.system.window.WindowGraphicContext;

import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class GLFWGraphicContext implements WindowGraphicContext, AutoCloseable {
	private long handler;

	public GLFWGraphicContext(long handler) {
		if (handler == NULL)
			throw new IllegalArgumentException("Null window handler");

		this.handler = handler;
	}

	@Override
	public boolean isAvailable() {
		return this.handler != NULL;
	}

	protected long getHandler() {
		return this.handler;
	}

	@Override
	public void close() {
		this.handler = NULL;
	}
}
