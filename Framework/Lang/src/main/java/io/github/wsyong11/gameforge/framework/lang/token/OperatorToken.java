package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;

public class OperatorToken extends Token{
	public OperatorToken(@NotNull String token, int charIndex) {
		super(token, charIndex);
	}

	@NotNull
	@Override
	protected String getTypeName() {
		return "Operator";
	}
}
