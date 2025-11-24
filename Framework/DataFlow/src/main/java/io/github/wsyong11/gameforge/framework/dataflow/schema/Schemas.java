package io.github.wsyong11.gameforge.framework.dataflow.schema;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.dataflow.schema.type.SchemaType;
import io.github.wsyong11.gameforge.framework.dataflow.schema.type.StringSchemaType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Schemas {
	public static final StringSchemaType IDENTIFIER_SCHEMA_TYPE = SchemaType
		.newString()
		.pattern(Identifier.MATCH_REGEX)
		.build();
}
