package io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

// TODO: 2025/11/22
public class EnumSchemaTypeBuilder extends DefaultSchemaTypeBuilder<EnumSchemaTypeBuilder, String> {
	@NotNull
	public EnumSchemaTypeBuilder ignoreCase() {
		return this.ignoreCase(true);
	}

	@NotNull
	public EnumSchemaTypeBuilder ignoreCase(boolean ignoreCase) {
		return this;
	}

	@NotNull
	public EnumSchemaTypeBuilder add(@NotNull String... enumValue) {
		Objects.requireNonNull(enumValue, "enumValue is null");
		return this.add(List.of(enumValue));
	}

	@NotNull
	public EnumSchemaTypeBuilder add(@NotNull Iterable<String> enumValue) {
		Objects.requireNonNull(enumValue, "enumValue is null");

		return this;
	}

	@NotNull
	@Override
	public SchemaType build() {
		return null;
	}
}
