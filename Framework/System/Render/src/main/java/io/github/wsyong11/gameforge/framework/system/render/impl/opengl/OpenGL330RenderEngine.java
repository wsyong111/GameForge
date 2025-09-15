package io.github.wsyong11.gameforge.framework.system.render.impl.opengl;

import io.github.wsyong11.gameforge.framework.system.render.engine.RenderEngineContext;
import io.github.wsyong11.gameforge.framework.system.render.engine.RenderSystemContext;
import io.github.wsyong11.gameforge.framework.system.window.WindowConfigBuilder;
import io.github.wsyong11.gameforge.framework.system.window.WindowGraphicContext;
import io.github.wsyong11.gameforge.framework.system.window.WindowGraphicsConfig;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

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
