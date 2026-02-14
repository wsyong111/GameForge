package io.github.wsyong11.gameforge.util.concurrent;

import com.google.common.collect.ForwardingIterator;
import com.google.common.util.concurrent.ForwardingBlockingQueue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class LimitedCapacityBlockingQueue<E> extends ForwardingBlockingQueue<E> {
	private final BlockingQueue<E> delegate;
	private final int maxCapacity;

	private final Semaphore semaphore;

	public LimitedCapacityBlockingQueue(@NotNull BlockingQueue<E> delegate, int maxCapacity) {
		Objects.requireNonNull(delegate, "delegate is null");

		if (maxCapacity <= 0)
			throw new IllegalArgumentException("Max capacity is negative or zero");

		this.delegate = delegate;
		this.maxCapacity = maxCapacity;
		this.semaphore = new Semaphore(maxCapacity);
	}

	@NotNull
	@Override
	protected BlockingQueue<E> delegate() {
		return this.delegate;
	}

	@Override
	public void put(@NotNull E element) throws InterruptedException {
		this.semaphore.acquire();
		try {
			super.put(element);
		} catch (Throwable e) {
			this.semaphore.release();
			throw e;
		}
	}

	@Override
	public boolean add(@NotNull E element) {
		if (!this.semaphore.tryAcquire())
			throw new IllegalStateException("The queue is full");

		boolean success;
		try {
			success = super.add(element);
		} catch (Throwable e) {
			this.semaphore.release();
			throw e;
		}

		if (!success)
			this.semaphore.release();

		return success;
	}

	@Override
	public boolean offer(@NotNull E element) {
		if (!this.semaphore.tryAcquire())
			return false;

		boolean success;
		try {
			success = super.offer(element);
		} catch (Throwable e) {
			this.semaphore.release();
			throw e;
		}

		if (!success)
			this.semaphore.release();

		return success;
	}

	@Override
	public boolean offer(@NotNull E element, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
		Objects.requireNonNull(unit, "unit is null");

		if (!this.semaphore.tryAcquire(timeout, unit))
			return false;

		boolean success;
		try {
			success = super.offer(element, timeout, unit);
		} catch (Throwable e) {
			this.semaphore.release();
			throw e;
		}

		if (!success)
			this.semaphore.release();

		return success;
	}

	@Override
	public boolean remove(@Nullable Object object) {
		boolean success = super.remove(object);
		if (success)
			this.semaphore.release();
		return success;
	}

	@Nullable
	@Override
	public E poll() {
		E element = super.poll();
		if (element != null)
			this.semaphore.release();

		return element;
	}

	@Nullable
	@Override
	public E poll(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
		Objects.requireNonNull(unit, "unit is null");

		E element = super.poll(timeout, unit);
		if (element != null)
			this.semaphore.release();

		return element;
	}

	@NotNull
	@Override
	public E take() throws InterruptedException {
		E element = super.take();
		this.semaphore.release();
		return element;
	}

	@Override
	public int drainTo(@NotNull Collection<? super E> c) {
		int count = super.drainTo(c);
		this.semaphore.release(count);
		return count;
	}

	@Override
	public int drainTo(@NotNull Collection<? super E> c, int maxElements) {
		int count = super.drainTo(c, maxElements);
		this.semaphore.release(count);
		return count;
	}

	@Override
	public void clear() {
		super.clear();
		this.semaphore.drainPermits();
		this.semaphore.release(this.maxCapacity);
	}

	@Override
	public int remainingCapacity() {
		return this.semaphore.availablePermits();
	}

	@NotNull
	@Override
	public Iterator<E> iterator() {
		Iterator<E> iterator = super.iterator();
		return new ForwardingIterator<>() {
			@NotNull
			@Override
			protected Iterator<E> delegate() {
				return iterator;
			}

			@Override
			public void remove() {
				super.remove();
				semaphore.release();
			}
		};
	}
}
