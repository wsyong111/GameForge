package io.github.wsyong11.gameforge.framework.system.render.engine;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.render.renderer.Renderer;
import io.github.wsyong11.gameforge.framework.system.render.shader.ShaderManager;
import io.github.wsyong11.gameforge.framework.system.window.WindowConfigBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RenderEngine extends AutoCloseable {
	void init();

	@NotNull
	Identifier getId();

	@NotNull
	ShaderManager getShaderManager();

	void buildWindow(@NotNull WindowConfigBuilder builder);

	void preRender();

	void render(@NotNull List<Renderer> renderers);

	@Override
	void close();
}
