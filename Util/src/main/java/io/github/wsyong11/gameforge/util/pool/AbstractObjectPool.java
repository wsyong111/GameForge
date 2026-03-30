package io.github.wsyong11.gameforge.util.pool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Queue;

public abstract class AbstractObjectPool<T> implements ObjectPool<T> {
	private final int maxSize;

	private final Queue<T> pool;

	public AbstractObjectPool(int maxSize, @NotNull Queue<T> queue) {
		Objects.requireNonNull(queue, "queue is null");

		this.maxSize = maxSize;
		this.pool = queue;
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

	@Nullable
	@Override
	public T tryAcquire() {
		return this.pool.poll();
	}

	@Override
	public void release(@Nullable T obj) {
		if (obj == null)
			return;

		this.resetObject(obj);
		if (this.pool.size() < this.maxSize)
			this.pool.offer(obj);
	}

	@Override
	public boolean tryRelease(@Nullable T obj) {
		if (obj == null)
			return false;

		this.resetObject(obj);
		if (this.pool.size() >= this.maxSize)
			return false;

		this.pool.offer(obj);
		return true;
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

