package io.github.wsyong11.gameforge.framework.dataflow.schema;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder.SchemaTypeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SchemaBuilder {
	@Nullable
	private SchemaType type = null;

	@NotNull
	public SchemaBuilder type(@NotNull SchemaType type) {
		Objects.requireNonNull(type, "type is null");
		this.type = type;
		return this;
	}

	@NotNull
	public SchemaBuilder type(@NotNull SchemaTypeBuilder<?> type) {
		Objects.requireNonNull(type, "type is null");
		this.type = type.build();
		return this;
	}

	@NotNull
	public Schema build() {
		Objects.requireNonNull(this.type, "Missing type");
		return new DefaultSchema(this.type);
	}
}