package io.github.wsyong11.gameforge.framework.system.render.impl.base;

import io.github.wsyong11.gameforge.framework.system.render.engine.RenderEngine;
import io.github.wsyong11.gameforge.framework.system.render.engine.RenderEngineContext;
import io.github.wsyong11.gameforge.framework.system.render.listener.RendererListener;
import io.github.wsyong11.gameforge.framework.system.render.renderer.Renderer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractRenderEngine implements RenderEngine {
	private final RenderEngineContext engineContext;

	private final RendererListener rendererListener;

	public AbstractRenderEngine(@NotNull RenderEngineContext context) {
		this.engineContext = context;

		this.rendererListener = new RendererListener() {
			@Override
			public void onRendererRegister(@NotNull Renderer renderer) {
				AbstractRenderEngine.this.onRendererRegister(renderer);
			}

			@Override
			public void onRendererUnregister(@NotNull Renderer renderer) {
				AbstractRenderEngine.this.onRendererUnregister(renderer);
			}
		};
	}

	protected void onRendererRegister(@NotNull Renderer renderer) {
		Objects.requireNonNull(renderer, "renderer is null");
	}

	protected void onRendererUnregister(@NotNull Renderer renderer) {
		Objects.requireNonNull(renderer, "renderer is null");
	}

	@NotNull
	protected RenderEngineContext getEngineContext() {
		return this.engineContext;
	}

	@Override
	public void init() {
		this.engineContext.registerRendererListener(this.rendererListener);
	}

	@Override
	public void close() {
		this.engineContext.unregisterRendererListener(this.rendererListener);
	}
}
