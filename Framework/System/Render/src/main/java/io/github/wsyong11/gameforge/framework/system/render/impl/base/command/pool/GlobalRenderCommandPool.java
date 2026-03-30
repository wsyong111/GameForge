package io.github.wsyong11.gameforge.framework.system.render.impl.base.command.pool;

import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.RenderCommands;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.command.RenderCommand;
import io.github.wsyong11.gameforge.util.pool.ObjectPool;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Supplier;

public class GlobalRenderCommandPool extends AbstractRenderCommandPool {
	private final int poolMaxSize;

	private final Object resizeLock;
	private volatile AtomicReferenceArray<ObjectPool<RenderCommand>> pools;

	public GlobalRenderCommandPool(int poolMaxSize) {
		this.poolMaxSize = poolMaxSize;

		this.resizeLock = new Object();
		this.pools = new AtomicReferenceArray<>(4);
	}

	private void ensurePoolSize(int id) {
		if (id < this.pools.length())
			return;

		synchronized (this.resizeLock) {
			AtomicReferenceArray<ObjectPool<RenderCommand>> current = this.pools;
			if (id < current.length())
				return;

			int newSize = Math.max(current.length() * 2, id + 1);
			AtomicReferenceArray<ObjectPool<RenderCommand>> newPools = new AtomicReferenceArray<>(newSize);

			for (int i = 0; i < current.length(); i++)
				newPools.set(i, current.get(i));

			this.pools = newPools;
		}
	}

	@Nullable
	@Override
	protected ObjectPool<RenderCommand> getPool(int id, @Nullable Class<RenderCommand> type) {
		assert type == null || id == RenderCommands.getId(type);
		while (true) {
			AtomicReferenceArray<ObjectPool<RenderCommand>> current = this.pools;

			if (id >= current.length()) {
				this.ensurePoolSize(id);
				continue;
			}

			ObjectPool<RenderCommand> pool = current.get(id);
			if (pool != null)
				return pool;

			if (type == null)
				return null;

			Supplier<RenderCommand> factory = RenderCommands.getFactory(type);
			ObjectPool<RenderCommand> newPool = ObjectPool.createConcurrent(
				this.poolMaxSize,
				factory,
				RenderCommand::reset
			);
			if (current.compareAndSet(id, null, newPool))
				return newPool;

			Thread.onSpinWait();
		}
	}
}
