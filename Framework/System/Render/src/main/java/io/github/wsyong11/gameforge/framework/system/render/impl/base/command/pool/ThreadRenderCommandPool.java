package io.github.wsyong11.gameforge.framework.system.render.impl.base.command.pool;

import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.RenderCommands;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.command.RenderCommand;
import io.github.wsyong11.gameforge.util.pool.ObjectPool;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Supplier;

public class ThreadRenderCommandPool extends AbstractRenderCommandPool {
	private final int poolMaxSize;
	private ObjectPool<RenderCommand>[] pools;

	@SuppressWarnings("unchecked")
	public ThreadRenderCommandPool(int poolMaxSize) {
		this.poolMaxSize = poolMaxSize;

		this.pools = (ObjectPool<RenderCommand>[]) new ObjectPool<?>[4];
	}

	private void ensurePoolSize(int id) {
		if (id < this.pools.length)
			return;

		int newSize = Math.max(this.pools.length * 2, id + 1);
		this.pools = Arrays.copyOf(this.pools, newSize);
	}

	@Nullable
	@Override
	protected ObjectPool<RenderCommand> getPool(int id, @Nullable Class<RenderCommand> type) {
		assert type == null || id == RenderCommands.getId(type);

		this.ensurePoolSize(id);

		ObjectPool<RenderCommand> pool = this.pools[id];
		if (pool != null)
			return pool;

		if (type == null)
			return null;

		Supplier<RenderCommand> factory = RenderCommands.getFactory(type);
		ObjectPool<RenderCommand> newPool = ObjectPool.create(
			this.poolMaxSize,
			factory,
			RenderCommand::reset
		);

		this.pools[id] = newPool;
		return newPool;
	}
}
