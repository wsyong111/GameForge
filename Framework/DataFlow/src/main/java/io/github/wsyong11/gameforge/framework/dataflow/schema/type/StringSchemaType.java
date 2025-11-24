package io.github.wsyong11.gameforge.framework.dataflow.schema.type;

import io.github.wsyong11.gameforge.util.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Pattern;

public class StringSchemaType extends DefaultValueSchemaType<String> {
	private final int minLength;
	private final int maxLength;
	@Nullable
	private final Pattern pattern;

	public StringSchemaType(@Nullable String defaultValue, int minLength, int maxLength, @Nullable Pattern pattern) {
		super(defaultValue);
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.pattern = pattern;
	}

	public int getMinLength() {
		return this.minLength;
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	@Nullable
	public Pattern getPattern() {
		return this.pattern;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		StringSchemaType that = (StringSchemaType) o;
		return this.minLength == that.minLength
			&& this.maxLength == that.maxLength
			&& Objects.equals(this.pattern, that.pattern);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.minLength, this.maxLength, this.pattern);
	}

	@Override
	public String toString() {
		return "String{" + StringUtils.joinNonNull(" ",
			StringUtils.formatRange(this.minLength, this.maxLength),
			this.pattern == null ? null : "p\"" + StringEscapeUtils.escapeJava(this.pattern.pattern()) + "\"",
			this.getDefaultValue() == null ? null : "d\"" + StringEscapeUtils.escapeJava(this.getDefaultValue()) + "\""
		) + "}";
	}
}
