package io.github.wsyong11.gameforge.framework.system.render.engine;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.render.Renderer;
import io.github.wsyong11.gameforge.framework.system.window.WindowConfigBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RenderEngine extends AutoCloseable {
	void init();

	@NotNull
	Identifier getId();

	void buildWindow(@NotNull WindowConfigBuilder builder);

	void preRender();

	void render(@NotNull List<Renderer> renderers);

	@Override
	void close();
}
