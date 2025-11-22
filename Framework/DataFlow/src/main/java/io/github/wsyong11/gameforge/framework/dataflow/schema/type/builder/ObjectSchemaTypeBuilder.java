package io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.ObjectSchemaType;
import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

// TODO: 2025/11/22
public class ObjectSchemaTypeBuilder extends SchemaTypeBuilder<ObjectSchemaTypeBuilder> {
	private final Map<String, SchemaType> fields = new HashMap<>();
	private final Set<String> requireFields = new HashSet<>();

	@NotNull
	public ObjectSchemaTypeBuilder requireField(@NotNull String name, @NotNull SchemaType type) {
		Objects.requireNonNull(name, "name is null");
		Objects.requireNonNull(type, "type is null");

		this.fields.put(name, type);
		this.requireFields.add(name);
		return this;
	}

	@NotNull
	public ObjectSchemaTypeBuilder requireField(@NotNull String name, @NotNull SchemaTypeBuilder<?> type) {
		Objects.requireNonNull(name, "name is null");
		Objects.requireNonNull(type, "type is null");
		return this.requireField(name, type.build());
	}

	@NotNull
	public ObjectSchemaTypeBuilder field(@NotNull String name, @NotNull SchemaType type) {
		Objects.requireNonNull(name, "name is null");
		Objects.requireNonNull(type, "type is null");

		this.fields.put(name, type);
		return this;
	}

	public ObjectSchemaTypeBuilder field(@NotNull String name, @NotNull SchemaTypeBuilder<?> type) {
		Objects.requireNonNull(name, "name is null");
		Objects.requireNonNull(type, "type is null");
		return this.field(name, type.build());
	}

	@NotNull
	@Override
	public ObjectSchemaType build() {
		return new ObjectSchemaType(this.fields, this.requireFields);
	}
}
