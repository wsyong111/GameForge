package io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.ArraySchemaType;
import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO: 2025/11/22
public class ArraySchemaTypeBuilder extends SchemaTypeBuilder<ArraySchemaTypeBuilder> {
	@Nullable
	private SchemaType itemType = null;
	private int minItems = 0;
	private int maxItems = Integer.MAX_VALUE;
	private boolean uniqueItems = false;

	@NotNull
	public ArraySchemaTypeBuilder itemType(@Nullable SchemaTypeBuilder<?> type) {
		return this.itemType(type == null ? null : type.build());
	}

	@NotNull
	public ArraySchemaTypeBuilder itemType(@Nullable SchemaType type) {
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
