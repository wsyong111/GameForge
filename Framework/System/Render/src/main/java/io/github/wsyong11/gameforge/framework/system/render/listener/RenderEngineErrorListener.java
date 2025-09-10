package io.github.wsyong11.gameforge.framework.system.render.listener;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import org.jetbrains.annotations.NotNull;

public interface RenderEngineErrorListener extends IListener {
	void onRenderEngineError(@NotNull LogLevel level, @NotNull String message, int code);
}
