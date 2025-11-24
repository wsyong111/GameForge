package io.github.wsyong11.gameforge.framework.dataflow.schema.type;

import io.github.wsyong11.gameforge.util.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ObjectSchemaType implements SchemaType {
	private final Map<String, SchemaType> fields;
	private final Set<String> requireFields;
	@Nullable
	private final SchemaType additionalPropertiesType;
	@Nullable
	private final Pattern additionalPropertyPattern;

	public ObjectSchemaType(
		@NotNull Map<String, SchemaType> fields,
		@NotNull Set<String> requireFields,
		@Nullable SchemaType additionalPropertiesType,
		@Nullable Pattern additionalPropertyPattern
	) {
		Objects.requireNonNull(fields, "fields is null");
		Objects.requireNonNull(requireFields, "requireFields is null");

		this.fields = Map.copyOf(fields);
		this.requireFields = Set.copyOf(requireFields);
		this.additionalPropertiesType = additionalPropertiesType;
		this.additionalPropertyPattern = additionalPropertyPattern;
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

	@Nullable
	public SchemaType getAdditionalPropertiesType() {
		return this.additionalPropertiesType;
	}

	@Nullable
	public Pattern getAdditionalPropertyPattern() {
		return this.additionalPropertyPattern;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ObjectSchemaType that = (ObjectSchemaType) o;
		return Objects.equals(this.fields, that.fields)
			&& Objects.equals(this.requireFields, that.requireFields)
			&& Objects.equals(this.additionalPropertiesType, that.additionalPropertiesType)
			&& Objects.equals(this.additionalPropertyPattern, that.additionalPropertyPattern);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.fields, this.requireFields, this.additionalPropertiesType, this.additionalPropertyPattern);
	}

	@NotNull
	@Override
	public String toString() {
		String fields = this.fields
			.entrySet()
			.stream()
			.map(entry -> "%s: %s%s".formatted(
				entry.getKey(),
				this.requireFields.contains(entry.getKey()) ? "[require] " : "",
				entry.getValue()))
			.collect(Collectors.joining(", "));
		return "Object{" + StringUtils.joinNonNull(" ",
			"{" + fields + "}",
			this.additionalPropertiesType == null ? null : "additionalProperties: " + this.additionalPropertiesType,
			this.additionalPropertyPattern == null ? null : "pattern: \"" + StringEscapeUtils.escapeJava(this.additionalPropertyPattern.pattern()) + "\""
		) + "}";
	}
}
