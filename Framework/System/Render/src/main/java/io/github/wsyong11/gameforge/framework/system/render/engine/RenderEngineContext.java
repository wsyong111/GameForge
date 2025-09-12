package io.github.wsyong11.gameforge.framework.system.render.engine;

import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.render.listener.LogicSizeListener;
import org.jetbrains.annotations.NotNull;

public interface RenderEngineContext extends RenderSystemContext {
	void error(@NotNull LogLevel level, @NotNull String message, int code);

	void registerLogicSizeListener(@NotNull LogicSizeListener listener);

	void unregisterLogicSizeListener(@NotNull LogicSizeListener listener);
}
