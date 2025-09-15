package io.github.wsyong11.gameforge.framework.system.render.engine;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.window.WindowConfigBuilder;
import org.jetbrains.annotations.NotNull;

public interface RenderEngine extends AutoCloseable {
	@NotNull
	Identifier getId();

	void buildWindow(@NotNull WindowConfigBuilder builder);

	@Override
	void close();
}
