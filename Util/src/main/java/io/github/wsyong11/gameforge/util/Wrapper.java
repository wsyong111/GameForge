package io.github.wsyong11.gameforge.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public abstract class Wrapper<T> {
	@UnknownNullability
	private T delegate;

	public Wrapper() { /* no-op */ }

	public Wrapper(@Nullable T delegate) {
		this.delegate = delegate;
	}

	protected void setDelegate(@Nullable T delegate) {
		this.delegate = delegate;
	}

	@Nullable
	protected T getDelegate() {
		return this.delegate;
	}

	protected void assertDelegate() {
		if (this.delegate == null)
			throw new NullPointerException("Implementation instance is not set");
	}

	@Override
	public String toString() {
		String name = this.getClass().getSimpleName();
		return this.delegate != null
			? name + "[" + this.delegate + "]"
			: name + "[null]";
	}

	@NotNull
	protected T delegate() {
		this.assertDelegate();
		return this.delegate;
	}
}
