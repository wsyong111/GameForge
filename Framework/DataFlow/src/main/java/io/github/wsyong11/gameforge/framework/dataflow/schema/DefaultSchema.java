package io.github.wsyong11.gameforge.framework.dataflow.schema;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DefaultSchema implements Schema {
	private final SchemaType type;

	public DefaultSchema(@NotNull SchemaType type) {
		Objects.requireNonNull(type, "type is null");
		this.type = type;
	}

	@NotNull
	@Override
	public SchemaType getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return "Schema{" + this.type + "}";
	}
}
