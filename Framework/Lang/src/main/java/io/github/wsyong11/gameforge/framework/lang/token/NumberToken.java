package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;

public class NumberToken extends Token{
	public NumberToken(@NotNull String token, int charIndex) {
		super(token, charIndex);
	}

	@NotNull
	@Override
	protected  String getTypeName() {
		return "Number";
	}
}
