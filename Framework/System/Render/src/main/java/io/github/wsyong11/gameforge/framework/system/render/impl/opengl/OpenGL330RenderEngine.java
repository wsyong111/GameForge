package io.github.wsyong11.gameforge.framework.system.render.impl.opengl;

import io.github.wsyong11.gameforge.framework.system.render.engine.RenderEngineContext;
import org.jetbrains.annotations.NotNull;

public class OpenGL330RenderEngine extends OpenGLRenderEngine {
	public OpenGL330RenderEngine(@NotNull RenderEngineContext context) {
		super(context);
	}

	@Override
	protected int getVersionMinor() {
		return 3;
	}

	@Override
	protected int getVersionMajor() {
		return 3;
	}
}
