package io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import org.checkerframework.checker.units.qual.N;
import org.checkerframework.checker.units.qual.min;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO: 2025/11/22
public class IntegerSchemaTypeBuilder extends DefaultSchemaTypeBuilder<IntegerSchemaTypeBuilder, Number> {
	@NotNull
	public IntegerSchemaTypeBuilder minimum(@Nullable Number min) {
		return this;
	}

	@NotNull
	public IntegerSchemaTypeBuilder maximum(@Nullable Number max) {
		return this;
	}

	@NotNull
	public IntegerSchemaTypeBuilder range(@Nullable Number min, @Nullable Number max) {
		return this.minimum(min).maximum(max);
	}

	@NotNull
	@Override
	public SchemaType build() {
		return null;
	}
}
