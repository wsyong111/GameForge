package io.github.wsyong11.gameforge.framework.dataflow.schema.type;

import io.github.wsyong11.gameforge.util.StreamUtils;
import io.github.wsyong11.gameforge.util.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EnumSchemaType extends DefaultValueSchemaType<String> {
	private final boolean ignoreCase;
	private final List<String> enumValues;

	public EnumSchemaType(@Nullable String defaultValue, boolean ignoreCase, @NotNull List<String> enumValues) {
		super(defaultValue);
		Objects.requireNonNull(enumValues, "enumValues is null");

		this.ignoreCase = ignoreCase;
		this.enumValues = List.copyOf(enumValues);
	}

	public boolean isIgnoreCase() {
		return this.ignoreCase;
	}

	@NotNull
	@UnmodifiableView
	public List<String> getEnumValues() {
		return this.enumValues;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		EnumSchemaType that = (EnumSchemaType) o;
		return this.ignoreCase == that.ignoreCase
			&& Objects.equals(this.enumValues, that.enumValues);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.ignoreCase, this.enumValues);
	}

	@Override
	public String toString() {
		return "Enum{" + StringUtils.joinNonNull(" ",
			this.ignoreCase ? "ignoreCase" : null,
			"[" + this.enumValues
				.stream()
				.map(StringEscapeUtils::escapeJava)
				.map(StreamUtils.wrapText('"', '"'))
				.collect(Collectors.joining(", ")) + "]",
			this.getDefaultValue() == null ? null : "d\"" + StringEscapeUtils.escapeJava(this.getDefaultValue()) + "\""
		) + "}";
	}
}
