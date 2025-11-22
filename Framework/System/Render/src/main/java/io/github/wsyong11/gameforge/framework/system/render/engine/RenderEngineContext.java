package io.github.wsyong11.gameforge.framework.system.render.engine;

import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.render.listener.LogicSizeListener;
import io.github.wsyong11.gameforge.framework.system.render.listener.RendererListener;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import io.github.wsyong11.gameforge.framework.system.window.WindowGraphicContext;
import org.jetbrains.annotations.NotNull;

public interface RenderEngineContext extends RenderSystemContext {
	void error(@NotNull LogLevel level, @NotNull String message, int code);

	void registerLogicSizeListener(@NotNull LogicSizeListener listener);

	void unregisterLogicSizeListener(@NotNull LogicSizeListener listener);

	void registerRendererListener(@NotNull RendererListener listener);

	void unregisterRendererListener(@NotNull RendererListener listener);

	@NotNull
	ResourceProvider getResourceProvider();
}
