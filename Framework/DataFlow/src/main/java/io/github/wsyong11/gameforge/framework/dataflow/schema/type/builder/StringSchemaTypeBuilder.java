package io.github.wsyong11.gameforge.framework.dataflow.schema.type.builder;

import io.github.wsyong11.gameforge.framework.dataflow.schema.type.StringSchemaType;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class StringSchemaTypeBuilder extends DefaultSchemaTypeBuilder<StringSchemaTypeBuilder, String> {
	private int minLength = 0;
	private int maxLength = Integer.MAX_VALUE;
	@Nullable
	private Pattern pattern = null;

	@NotNull
	public StringSchemaTypeBuilder minLength(int length) {
		this.minLength = length;
		return this;
	}

	@NotNull
	public StringSchemaTypeBuilder maxLength(int length) {
		this.maxLength = length;
		return this;
	}

	@NotNull
	public StringSchemaTypeBuilder pattern(@Nullable @Language("RegExp") String pattern) {
		return this.pattern(pattern==null?null:Pattern.compile(pattern));
	}

	@NotNull
	public StringSchemaTypeBuilder pattern(@Nullable Pattern pattern) {
		this.pattern = pattern;
		return this;
	}

	@NotNull
	@Override
	public StringSchemaType build() {
		return new StringSchemaType(this.defaultValue, this.minLength, this.maxLength, this.pattern);
	}
}
