package io.github.wsyong11.gameforge.framework.system.render.impl.base.command.pool;

import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.RenderCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RenderCommandPool {
	@SuppressWarnings("unchecked")
	@Nullable
	<T extends RenderCommand> T tryAlloc(@NotNull Class<T> type);

	boolean tryFree(@NotNull RenderCommand cmd);
}
