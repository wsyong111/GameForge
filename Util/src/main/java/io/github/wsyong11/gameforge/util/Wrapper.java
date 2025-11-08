package io.github.wsyong11.gameforge.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;

public abstract class Wrapper<T> {
	@UnknownNullability
	private volatile T delegate;

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
			throw new NullPointerException(this.getClass().getSimpleName() + " delegate instance is not set");
	}

	@NotNull
	protected T delegate() {
		this.assertDelegate();
		return this.delegate;
	}

	@Override
	public boolean equals(Object obj) {
		if (this.delegate == null) return false;

		if (obj == null) return false;
		if (obj == this || obj == this.delegate) return true;
		if (!(obj instanceof Wrapper<?> that)) return false;

		return Objects.equals(this.delegate, that.delegate);
	}

	@Override
	public int hashCode() {
		return this.delegate == null ? super.hashCode() : this.delegate.hashCode();
	}

	@Override
	public String toString() {
		String name = this.getClass().getSimpleName();
		return this.delegate != null
			? name + "[" + this.delegate + "]"
			: name + "[null]";
	}
}
