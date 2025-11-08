package io.github.wsyong11.gameforge.framework.app;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BootstrapConfig<T> {
	@NotNull
	public static <V> BootstrapConfig<V> create(@NotNull String name, @NotNull Class<? super V> type) {
		return new BootstrapConfig<>(name, type);
	}

	private final String name;
	private final Class<? super T> type;

	public BootstrapConfig(@NotNull String name, @NotNull Class<? super T> type) {
		Objects.requireNonNull(name, "name is null");
		Objects.requireNonNull(type, "type is null");

		this.name = name;
		this.type = type;
	}

	@NotNull
	public String getName() {
		return this.name;
	}

	@NotNull
	public Class<? super T> getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BootstrapConfig<?> that = (BootstrapConfig<?>) o;
		return Objects.equals(this.name, that.name)
			&& Objects.equals(this.type, that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.type);
	}
}
