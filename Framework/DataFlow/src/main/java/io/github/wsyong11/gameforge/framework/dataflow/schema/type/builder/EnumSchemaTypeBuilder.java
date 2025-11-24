package io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.EnumSchemaType;
import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// TODO: 2025/11/22
public class EnumSchemaTypeBuilder extends DefaultSchemaTypeBuilder<EnumSchemaTypeBuilder, String> {
	private boolean ignoreCase = false;
	private final List<String> enumValues = new ArrayList<>();

	@NotNull
	public EnumSchemaTypeBuilder ignoreCase() {
		return this.ignoreCase(true);
	}

	@NotNull
	public EnumSchemaTypeBuilder ignoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
		return this;
	}

	@NotNull
	public EnumSchemaTypeBuilder add(@NotNull String... enumValues) {
		Objects.requireNonNull(enumValues, "enumValues is null");

		Collections.addAll(this.enumValues, enumValues);
		return this;
	}

	@NotNull
	public EnumSchemaTypeBuilder add(@NotNull Iterable<String> enumValues) {
		Objects.requireNonNull(enumValues, "enumValue is null");

		for (String enumValue : enumValues)
			this.enumValues.add(enumValue);
		return this;
	}

	@NotNull
	@Override
	public SchemaType build() {
		return new EnumSchemaType(this.defaultValue, this.ignoreCase, this.enumValues);
	}
}
