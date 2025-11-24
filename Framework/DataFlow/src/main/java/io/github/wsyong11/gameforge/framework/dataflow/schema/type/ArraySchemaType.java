package io.github.wsyong11.gameforge.framework.dataflow.schema.type;

import io.github.wsyong11.gameforge.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ArraySchemaType implements SchemaType {
	private final SchemaType itemType;
	private final int minItems;
	private final int maxItems;
	private final boolean uniqueItems;

	public ArraySchemaType(@NotNull SchemaType itemType, int minItems, int maxItems, boolean uniqueItems) {
		Objects.requireNonNull(itemType, "itemType is null");

		if (minItems < 0)
			throw new IllegalArgumentException("Min item count cannot be negative");

		if (maxItems < 0)
			throw new IllegalArgumentException("Max item count cannot be negative");

		if (maxItems < minItems)
			throw new IllegalArgumentException("Max item count is less than min item count");

		this.itemType = itemType;
		this.minItems = minItems;
		this.maxItems = maxItems;
		this.uniqueItems = uniqueItems;
	}

	public SchemaType getItemType() {
		return this.itemType;
	}

	public int getMinItems() {
		return this.minItems;
	}

	public int getMaxItems() {
		return this.maxItems;
	}

	public boolean isUniqueItems() {
		return this.uniqueItems;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ArraySchemaType that = (ArraySchemaType) o;
		return this.minItems == that.minItems
			&& this.maxItems == that.maxItems
			&& this.uniqueItems == that.uniqueItems
			&& Objects.equals(this.itemType, that.itemType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.itemType, this.minItems, this.maxItems, this.uniqueItems);
	}

	@Override
	public String toString() {
		return "Array{" + StringUtils.joinNonNull(" ",
			this.uniqueItems ? "unique" : null,
			StringUtils.formatRange(this.minItems, this.maxItems),
			this.itemType.toString()
		) + "}";
	}
}
