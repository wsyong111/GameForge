package io.github.wsyong11.gameforge.framework.dataflow.schema.type;

public class AnySchemaType implements SchemaType {
	public static final AnySchemaType INSTANCE = new AnySchemaType();

	protected AnySchemaType() { /* no-op */ }

	@Override
	public boolean equals(Object obj) {
    return obj != null && obj.getClass() == this.getClass();
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}

	@Override
	public String toString() {
		return "Any{}";
	}
}
