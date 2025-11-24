package io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.BooleanSchemaType;
import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import org.jetbrains.annotations.NotNull;

// TODO: 2025/11/22
public class BooleanSchemaTypeBuilder extends DefaultSchemaTypeBuilder<BooleanSchemaTypeBuilder, Boolean> {
	@NotNull
	@Override
	public SchemaType build() {
		return new BooleanSchemaType(this.defaultValue);
	}
}
