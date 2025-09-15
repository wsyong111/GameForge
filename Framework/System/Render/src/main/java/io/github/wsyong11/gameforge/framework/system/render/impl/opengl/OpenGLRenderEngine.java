package io.github.wsyong11.gameforge.framework.system.render.impl.opengl;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.render.engine.RenderEngine;
import io.github.wsyong11.gameforge.framework.system.render.engine.RenderEngineContext;
import io.github.wsyong11.gameforge.framework.system.window.WindowConfigBuilder;
import io.github.wsyong11.gameforge.framework.system.window.WindowGraphicContext;
import io.github.wsyong11.gameforge.framework.system.window.WindowGraphicsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class OpenGLRenderEngine implements RenderEngine {
	public static final Identifier ID = Identifier.withDefaultNamespace("opengl");

	private final RenderEngineContext engineContext;

	protected OpenGLRenderEngine(@NotNull RenderEngineContext engineContext) {
		this.engineContext = engineContext;
	}

	@NotNull
	protected RenderEngineContext getEngineContext() {
		return this.engineContext;
	}

	@NotNull
	@Override
	public Identifier getId() {
		return ID;
	}

	protected abstract int getVersionMinor();

	protected abstract int getVersionMajor();

	@Override
	public void buildWindow(@NotNull WindowConfigBuilder builder) {
		Objects.requireNonNull(builder, "builder is null");

		builder.api(WindowGraphicContext.OPENGL);
		builder.graphicsConfig(new WindowGraphicsConfig(
			this.getVersionMajor(),
			this.getVersionMinor(),
			this.engineContext.isDebug()
		));
	}

	@Override
	public void close() {

	}
}
