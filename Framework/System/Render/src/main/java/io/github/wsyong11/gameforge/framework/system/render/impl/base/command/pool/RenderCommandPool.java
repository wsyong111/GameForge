package io.github.wsyong11.gameforge.framework.system.render.impl.base.command.pool;

import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.command.RenderCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RenderCommandPool {
	@Nullable
	<T extends RenderCommand> T tryAcquire(@NotNull Class<T> type);

	boolean tryRelease(@NotNull RenderCommand cmd);
}
