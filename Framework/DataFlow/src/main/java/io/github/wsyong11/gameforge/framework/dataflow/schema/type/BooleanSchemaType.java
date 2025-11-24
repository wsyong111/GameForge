package io.github.wsyong11.gameforge.framework.dataflow.schema.type;

import org.jetbrains.annotations.Nullable;

public class BooleanSchemaType extends DefaultValueSchemaType<Boolean> {
	public BooleanSchemaType(@Nullable Boolean defaultValue) {
		super(defaultValue);
	}

	@Override
	public String toString() {
		return "Boolean{" + (this.getDefaultValue() == null ? "d " + this.getDefaultValue() : "") + "}";
	}
}
