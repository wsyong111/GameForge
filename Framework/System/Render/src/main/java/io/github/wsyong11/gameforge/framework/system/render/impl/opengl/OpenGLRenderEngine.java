package io.github.wsyong11.gameforge.framework.system.render.impl.opengl;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.render.Renderer;
import io.github.wsyong11.gameforge.framework.system.render.context.RenderContext;
import io.github.wsyong11.gameforge.framework.system.render.engine.RenderEngine;
import io.github.wsyong11.gameforge.framework.system.render.engine.RenderEngineContext;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.CommandRenderContext;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.RenderCommandStack;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.SimplePoseStack;
import io.github.wsyong11.gameforge.framework.system.render.listener.RendererListener;
import io.github.wsyong11.gameforge.framework.system.window.WindowConfigBuilder;
import io.github.wsyong11.gameforge.framework.system.window.WindowGraphicContext;
import io.github.wsyong11.gameforge.framework.system.window.WindowGraphicsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public abstract class OpenGLRenderEngine implements RenderEngine {
	public static final Identifier ID = Identifier.withDefaultNamespace("opengl");

	private final RenderEngineContext engineContext;

	private final SimplePoseStack poseStack;
	private final RenderCommandStack commandStack;
	private final RenderContext renderContext;

	private final RendererListener rendererListener;

	protected OpenGLRenderEngine(@NotNull RenderEngineContext engineContext) {
		Objects.requireNonNull(engineContext, "engineContext is null");

		this.engineContext = engineContext;

		this.poseStack = new SimplePoseStack();
		this.commandStack = new RenderCommandStack();
		this.renderContext = new CommandRenderContext(this.commandStack, this.poseStack);

		this.rendererListener = new RendererListener() {
			@Override
			public void onRendererRegister(@NotNull Renderer renderer) {
				rendererRegister(renderer);
			}

			@Override
			public void onRendererUnregister(@NotNull Renderer renderer) {
				rendererUnregister(renderer);
			}
		};
	}

	protected void rendererRegister(@NotNull Renderer renderer) {

	}

	protected void rendererUnregister(@NotNull Renderer renderer) {

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
	public void init() {
		this.engineContext.registerRendererListener(this.rendererListener);
	}

	@Override
	public void preRender() {
		this.poseStack.reset();
		this.commandStack.reset();
	}

	@Override
	public void render(@NotNull List<Renderer> renderers) {
		Objects.requireNonNull(renderers, "renderers is null");

		for (Renderer renderer : renderers) {
			renderer.render(this.renderContext);
		}
	}

	@Override
	public void close() {
		this.engineContext.unregisterRendererListener(this.rendererListener);
	}
}
