package io.github.wsyong11.gameforge.framework.dataflow.schema.type;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder.*;
import org.jetbrains.annotations.NotNull;

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
	static NumberSchemaTypeBuilder newNumber() {
		return new NumberSchemaTypeBuilder();
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

	@NotNull
	static AnySchemaType any() {
		return AnySchemaType.INSTANCE;
	}
}