package io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import org.jetbrains.annotations.NotNull;

public abstract class SchemaTypeBuilder<SELF extends SchemaTypeBuilder<SELF>> {
	@SuppressWarnings("unchecked")
	protected SELF cast() {
		return (SELF) this;
	}

	@NotNull
	public abstract SchemaType build();
}
