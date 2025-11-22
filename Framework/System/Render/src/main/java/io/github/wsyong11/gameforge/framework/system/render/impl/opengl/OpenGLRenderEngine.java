package io.github.wsyong11.gameforge.framework.system.render.impl.opengl;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.render.engine.RenderEngineContext;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.AbstractRenderEngine;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.SimplePoseStack;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.SimpleRendererContext;
import io.github.wsyong11.gameforge.framework.system.render.impl.opengl.shader.GLShaderManager;
import io.github.wsyong11.gameforge.framework.system.render.renderer.Renderer;
import io.github.wsyong11.gameforge.framework.system.render.renderer.RendererContext;
import io.github.wsyong11.gameforge.framework.system.render.shader.ShaderManager;
import io.github.wsyong11.gameforge.framework.system.window.WindowConfigBuilder;
import io.github.wsyong11.gameforge.framework.system.window.WindowGraphicContext;
import io.github.wsyong11.gameforge.framework.system.window.WindowGraphicsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class OpenGLRenderEngine extends AbstractRenderEngine {
	public static final Identifier ID = Identifier.withDefaultNamespace("opengl");

	private final GLShaderManager shaderManager;

	private final Map<Renderer, RendererContext> rendererContextMap;

	private final SimplePoseStack poseStack;

	protected OpenGLRenderEngine(@NotNull RenderEngineContext engineContext) {
		super(engineContext);

		this.shaderManager = new GLShaderManager(engineContext, engineContext.getResourceProvider());

		this.rendererContextMap = new IdentityHashMap<>();

		this.poseStack = new SimplePoseStack();
	}

	@Override
	protected void onRendererRegister(@NotNull Renderer renderer) {
		super.onRendererRegister(renderer);

		SimpleRendererContext context = new SimpleRendererContext();
		this.rendererContextMap.put(renderer, context);
		renderer.init(context);
	}

	@Override
	protected void onRendererUnregister(@NotNull Renderer renderer) {
		super.onRendererUnregister(renderer);

		this.rendererContextMap.remove(renderer);
	}

	@NotNull
	@Override
	public Identifier getId() {
		return ID;
	}

	protected abstract int getVersionMinor();

	protected abstract int getVersionMajor();

	@NotNull
	@Override
	public ShaderManager getShaderManager() {
		return this.shaderManager;
	}

	@Override
	public void buildWindow(@NotNull WindowConfigBuilder builder) {
		Objects.requireNonNull(builder, "builder is null");

		builder.api(WindowGraphicContext.OPENGL);
		builder.graphicsConfig(new WindowGraphicsConfig(
			this.getVersionMajor(),
			this.getVersionMinor(),
			this.getEngineContext().isDebug()
		));
	}

	@Override
	public void preRender() {
		this.poseStack.reset();
	}

	@Override
	public void render(@NotNull List<Renderer> renderers) {
		Objects.requireNonNull(renderers, "renderers is null");

		for (Renderer renderer : renderers) {
//			this.renderContext.beginRender(renderer);
//
			RendererContext context = this.rendererContextMap.get(renderer);
//			renderer.render(context, this.renderContext);
//
//			this.renderContext.endRender(renderer);
		}
	}

	@Override
	public void close() {
		super.close();

		this.poseStack.reset();
		this.rendererContextMap.clear();
	}
}
