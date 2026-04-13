package io.github.wsyong11.gameforge.framework.spi;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ExtensionType<T> {
	private final Class<T> type;

	public ExtensionType(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");
		this.type = type;
	}

	@NotNull
	public Class<T> getType() {
		return this.type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ExtensionType<?> that = (ExtensionType<?>) o;
		return Objects.equals(this.type, that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.type);
	}

	@Override
	public String toString() {
		return "ExceptionType[" + this.type.getName() + "]";
	}
}
