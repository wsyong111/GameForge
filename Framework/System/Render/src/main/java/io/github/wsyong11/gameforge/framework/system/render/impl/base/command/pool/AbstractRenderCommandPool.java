package io.github.wsyong11.gameforge.framework.system.render.impl.base.command.pool;

import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.RenderCommands;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.command.RenderCommand;
import io.github.wsyong11.gameforge.util.pool.ObjectPool;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractRenderCommandPool implements RenderCommandPool {
	@Nullable
	@Contract("_, !null -> !null; _, null -> _")
	protected abstract ObjectPool<RenderCommand> getPool(int id, @Nullable Class<RenderCommand> type);

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T extends RenderCommand> T tryAcquire(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		int id = RenderCommands.getId(type);
		ObjectPool<RenderCommand> pool = this.getPool(id, (Class<RenderCommand>) type);
		return (T) pool.tryAcquire();
	}

	@Override
	public boolean tryRelease(@NotNull RenderCommand cmd) {
		Objects.requireNonNull(cmd, "cmd is null");

		int id = cmd.getTypeId();
		ObjectPool<RenderCommand> pool = this.getPool(id, null);
		return pool != null && pool.tryRelease(cmd);
	}
}
