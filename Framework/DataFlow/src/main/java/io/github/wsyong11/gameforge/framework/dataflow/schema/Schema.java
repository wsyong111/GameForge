package io.github.wsyong11.gameforge.framework.dataflow.schema;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import org.jetbrains.annotations.NotNull;

public interface Schema {
	@NotNull
	static SchemaBuilder builder() {
		return new SchemaBuilder();
	}

	@NotNull
	SchemaType getType();
}
