package io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.AnySchemaType;
import io.github.wsyong11.gameforge.framework.dataflow.schema.type.ArraySchemaType;
import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// TODO: 2025/11/22
public class ArraySchemaTypeBuilder extends SchemaTypeBuilder<ArraySchemaTypeBuilder> {
	@Nullable
	private SchemaType itemType = AnySchemaType.INSTANCE;
	private int minItems = 0;
	private int maxItems = Integer.MAX_VALUE;
	private boolean uniqueItems = false;

	@NotNull
	public ArraySchemaTypeBuilder itemType(@NotNull SchemaTypeBuilder<?> type) {
		Objects.requireNonNull(type, "type is null");
		return this.itemType(type.build());
	}

	@NotNull
	public ArraySchemaTypeBuilder itemType(@NotNull SchemaType type) {
		Objects.requireNonNull(type, "type is null");
		this.itemType = type;
		return this;
	}

	@NotNull
	public ArraySchemaTypeBuilder minItems(int count) {
		if (count < 0)
			throw new IllegalArgumentException("Min item count cannot be negative");
		this.minItems = count;
		return this;
	}

	@NotNull
	public ArraySchemaTypeBuilder maxItems(int count) {
		if (count < 0)
			throw new IllegalArgumentException("Max item count cannot be negative");
		this.maxItems = count;
		return this;
	}

	@NotNull
	public ArraySchemaTypeBuilder uniqueItems() {
		return this.uniqueItems(true);
	}

	@NotNull
	public ArraySchemaTypeBuilder uniqueItems(boolean unique) {
		this.uniqueItems = unique;
		return this;
	}

	@NotNull
	@Override
	public ArraySchemaType build() {
		return new ArraySchemaType(this.itemType, this.minItems, this.maxItems, this.uniqueItems);
	}
}
