package io.github.wsyong11.gameforge.framework.dataflow.schema;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Schema {
	@NotNull
	static SchemaBuilder builder() {
		return new SchemaBuilder();
	}

	@NotNull
	SchemaType getType();
}
