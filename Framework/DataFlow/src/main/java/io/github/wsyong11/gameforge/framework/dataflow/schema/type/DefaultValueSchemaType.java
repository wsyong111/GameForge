package io.github.wsyong11.gameforge.framework.dataflow.schema.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class DefaultValueSchemaType<T> implements SchemaType {
	@Nullable
	private final T defaultValue;

	protected DefaultValueSchemaType(@Nullable T defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Nullable
	public T getDefaultValue() {
		return this.defaultValue;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultValueSchemaType<?> that = (DefaultValueSchemaType<?>) o;
		return Objects.equals(this.defaultValue, that.defaultValue);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.defaultValue);
	}
}
