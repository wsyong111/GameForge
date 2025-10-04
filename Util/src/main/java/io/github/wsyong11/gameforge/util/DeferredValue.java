package io.github.wsyong11.gameforge.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DeferredValue<T> {
	@NotNull
	public static <V> DeferredValue<V> of() {
		return new DeferredValue<>();
	}

	@NotNull
	public static <V> DeferredValue<V> of(V value) {
		return new DeferredValue<>(value);
	}

	private volatile T value;
	private volatile T newValue;

	public DeferredValue() {
		this.value = null;
		this.newValue = null;
	}

	public DeferredValue(T value) {
		this.value = value;
		this.newValue = value;
	}

	protected boolean isEqualValue(@Nullable T value, @Nullable T newValue) {
		return Objects.equals(value, newValue);
	}

	public T getValue() {
		return this.value;
	}

	public T getNewValue() {
		return this.newValue;
	}

	public void setNewValue(T value) {
		this.newValue = value;
	}

	public boolean update() {
		synchronized (this) {
			if (this.isEqualValue(this.value, this.newValue))
				return false;

			this.value = this.newValue;
			return true;
		}
	}

	public boolean needUpdate() {
		return !this.isEqualValue(this.value, this.newValue);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DeferredValue<?> other)) return false;

		return Objects.equals(this.value, other.value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.value);
	}

	@Override
	public String toString() {
		T value = this.value;
		T newValue = this.newValue;

		if (this.isEqualValue(value, newValue))
			return "Deferred[" + value + "]";
		else
			return "Deferred[" + value + " > " + newValue + "]";
	}
}
