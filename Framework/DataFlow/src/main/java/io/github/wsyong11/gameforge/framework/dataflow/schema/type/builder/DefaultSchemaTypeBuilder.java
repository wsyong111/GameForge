package io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO: 2025/11/22
public abstract class DefaultSchemaTypeBuilder<SELF extends DefaultSchemaTypeBuilder<SELF, T>, T> extends SchemaTypeBuilder<SELF> {
	@Nullable
	protected T defaultValue = null;

	@NotNull
	public SELF defaultValue(@Nullable T value) {
		this.defaultValue = value;
		return this.cast();
	}
}
