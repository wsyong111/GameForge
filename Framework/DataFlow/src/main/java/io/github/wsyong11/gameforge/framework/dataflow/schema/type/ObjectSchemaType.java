package io.github.wsyong11.gameforge.framework.dataflow.schema.type;

import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectSchemaType implements SchemaType {
	private final Map<String, SchemaType> fields;
	private final Set<String> requireFields;

	public ObjectSchemaType(@NotNull Map<String, SchemaType> fields, @NotNull Set<String> requireFields) {
		Objects.requireNonNull(fields, "fields is null");
		Objects.requireNonNull(requireFields, "requireFields is null");

		this.fields = Map.copyOf(fields);
		this.requireFields = Set.copyOf(requireFields);
	}

	@NotNull
	@UnmodifiableView
	public Map<String, SchemaType> getFields() {
		return this.fields;
	}

	@NotNull
	@UnmodifiableView
	public Set<String> getRequireFields() {
		return this.requireFields;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ObjectSchemaType that = (ObjectSchemaType) o;
		return Objects.equals(this.fields, that.fields)
			&& Objects.equals(this.requireFields, that.requireFields);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.fields, this.requireFields);
	}

	@NotNull
	@Override
	public String toString() {
		return "Object{" + this.fields
			.entrySet()
			.stream()
			.map (entry -> (this.requireFields.contains(entry.getKey()) ? "[require] " : "") + entry.getValue())
			.collect(Collectors.joining(", ")) + "}";
	}
}
