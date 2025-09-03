package io.github.wsyong11.gameforge.util.concurrent;

public abstract class AbstractThreadLocal<T> extends ThreadLocal<T> {
	public AbstractThreadLocal() { /* no-op */ }

	@Override
	protected T initialValue() {
		return null;
	}

	@Override
	public abstract void remove();

	@Override
	public abstract void set(T value);

	@Override
	public abstract T get();
}
