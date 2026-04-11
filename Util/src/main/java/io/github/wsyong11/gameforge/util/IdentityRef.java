package io.github.wsyong11.gameforge.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class IdentityRef<T> {
	private final T ref;

	public IdentityRef(@NotNull T ref) {
		Objects.requireNonNull(ref, "ref is null");
		this.ref = ref;
	}

	@NotNull
	public T get() {
		return this.ref;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof IdentityRef<?> other
			&& this.ref == other.ref;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this.ref);
	}

	@Override
	public String toString() {
		return this.ref.toString();
	}
}
