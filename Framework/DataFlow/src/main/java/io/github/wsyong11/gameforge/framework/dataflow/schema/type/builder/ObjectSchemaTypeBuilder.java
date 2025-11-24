package io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.ObjectSchemaType;
import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

// TODO: 2025/11/22
public class ObjectSchemaTypeBuilder extends SchemaTypeBuilder<ObjectSchemaTypeBuilder> {
	private final Map<String, SchemaType> fields = new HashMap<>();
	private final Set<String> requireFields = new HashSet<>();
	private SchemaType additionalPropertiesType = null;
	private Pattern additionalPropertyPattern = null;

	@NotNull
	public ObjectSchemaTypeBuilder requireField(@NotNull String name, @NotNull SchemaType type) {
		Objects.requireNonNull(name, "name is null");
		Objects.requireNonNull(type, "type is null");

		this.field(name, type);
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

		if (this.fields.putIfAbsent(name, type) != null)
			throw new IllegalStateException("Field \"" + name + "\" has been added");
		return this;
	}

	@NotNull
	public ObjectSchemaTypeBuilder field(@NotNull String name, @NotNull SchemaTypeBuilder<?> type) {
		Objects.requireNonNull(name, "name is null");
		Objects.requireNonNull(type, "type is null");
		return this.field(name, type.build());
	}

	@NotNull
	public ObjectSchemaTypeBuilder additionalProperties(@Nullable SchemaType type) {
		this.additionalPropertiesType = type;
		this.additionalPropertyPattern = null;
		return this;
	}

	@NotNull
	public ObjectSchemaTypeBuilder additionalProperties(@Nullable SchemaTypeBuilder<?> type) {
		return this.additionalProperties(type == null ? null : type.build());
	}

	@NotNull
	public ObjectSchemaTypeBuilder additionalProperties(@NotNull Pattern keyPattern, @NotNull SchemaType type) {
		Objects.requireNonNull(keyPattern, "keyPattern is null");
		Objects.requireNonNull(type, "type is null");

		this.additionalPropertiesType = type;
		this.additionalPropertyPattern = keyPattern;
		return this;
	}


	@NotNull
	public ObjectSchemaTypeBuilder additionalProperties(@NotNull Pattern keyPattern, @NotNull SchemaTypeBuilder<?> type) {
		Objects.requireNonNull(keyPattern, "keyPattern is null");
		Objects.requireNonNull(type, "type is null");
		return this.additionalProperties(keyPattern, type.build());
	}

	@NotNull
	@Override
	public ObjectSchemaType build() {
		return new ObjectSchemaType(this.fields, this.requireFields, this.additionalPropertiesType, this.additionalPropertyPattern);
	}
}
