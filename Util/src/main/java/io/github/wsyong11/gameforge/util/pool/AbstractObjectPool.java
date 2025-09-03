package io.github.wsyong11.gameforge.util.pool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractObjectPool<T> implements ObjectPool<T> {
	private final int maxSize;

	private final Queue<T> pool;

	public AbstractObjectPool(int maxSize) {
		this.maxSize = maxSize;
		this.pool = new ConcurrentLinkedQueue<>();
	}

	@NotNull
	protected abstract T createObject();

	protected abstract void resetObject(@NotNull T obj);

	@NotNull
	@Override
	public T acquire() {
		T obj = this.pool.poll();
		return obj != null ? obj : this.createObject();
	}

	@Override
	public void release(@Nullable T obj) {
		if (obj == null) return;

		this.resetObject(obj);
		if (this.pool.size() < this.maxSize)
			this.pool.offer(obj);
	}

	@NotNull
	@Override
	public ObjectPool<T> prewarm(int count) {
		int toCreate = Math.min(count, this.maxSize - this.size());
		for (int i = 0; i < toCreate; i++)
			this.pool.offer(this.createObject());
		return this;
	}

	@Override
	public int size() {
		return this.pool.size();
	}

	@Override
	public int getMaxSize() {
		return this.maxSize;
	}
}

