package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;

public class StringToken extends Token {
	public StringToken(@NotNull String token, int charIndex) {
		super(token, charIndex);
	}

	@NotNull
	@Override
	protected String getTypeName() {
		return "String";
	}
}
