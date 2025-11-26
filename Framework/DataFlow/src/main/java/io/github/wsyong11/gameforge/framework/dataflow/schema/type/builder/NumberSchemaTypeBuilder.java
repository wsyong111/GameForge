package io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.NumberSchemaType;
import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class NumberSchemaTypeBuilder extends DefaultSchemaTypeBuilder<NumberSchemaTypeBuilder, Number> {
	@Nullable
	private Number minimum = null;
	@Nullable
	private Number maximum = null;

	@NotNull
	public NumberSchemaTypeBuilder minimum(@Nullable Number min) {
		this.minimum = min;
		return this;
	}

	@NotNull
	public NumberSchemaTypeBuilder maximum(@Nullable Number max) {
		this.maximum = max;
		return this;
	}

	@NotNull
	public NumberSchemaTypeBuilder range(@Nullable Number min, @Nullable Number max) {
		return this.minimum(min).maximum(max);
	}

	@NotNull
	@Override
	public SchemaType build() {
		ensureSameType(this.defaultValue, this.minimum, "defaultValue", "minimum");
		ensureSameType(this.defaultValue, this.maximum, "defaultValue", "maximum");
		ensureSameType(this.minimum, this.maximum, "minimum", "maximum");

		return new NumberSchemaType(this.defaultValue, this.minimum, this.maximum);
	}

	private static void ensureSameType(@Nullable Number a, @Nullable Number b, @NotNull String nameA, @NotNull String nameB) {
		Objects.requireNonNull(nameA, "nameA is null");
		Objects.requireNonNull(nameB, "nameB is null");

		if (a != null && b != null && a.getClass() != b.getClass()) {
			throw new IllegalArgumentException(nameA + " and " + nameB + " must be same Number type");
		}
	}
}
