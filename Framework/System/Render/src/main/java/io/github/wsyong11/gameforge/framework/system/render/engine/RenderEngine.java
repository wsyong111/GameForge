package io.github.wsyong11.gameforge.framework.system.render.engine;

import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.render.listener.LogicSizeListener;
import org.jetbrains.annotations.NotNull;

public interface RenderEngine extends AutoCloseable{
	@Override
	void close();
}
