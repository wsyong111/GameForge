package io.github.wsyong11.gameforge.framework.dataflow.schema.type;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SchemaType {
	@NotNull
	static ObjectSchemaTypeBuilder newObject() {
		return new ObjectSchemaTypeBuilder();
	}

	@NotNull
	static StringSchemaTypeBuilder newString() {
		return new StringSchemaTypeBuilder();
	}

	@NotNull
	static IntegerSchemaTypeBuilder newInteger() {
		return new IntegerSchemaTypeBuilder();
	}

	@NotNull
	static EnumSchemaTypeBuilder newEnum() {
		return new EnumSchemaTypeBuilder();
	}

	@NotNull
	static ArraySchemaTypeBuilder newArray() {
		return new ArraySchemaTypeBuilder();
	}

	@NotNull
	static BooleanSchemaTypeBuilder newBoolean() {
		return new BooleanSchemaTypeBuilder();
	}
}