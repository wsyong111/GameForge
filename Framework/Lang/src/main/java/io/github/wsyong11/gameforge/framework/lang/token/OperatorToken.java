package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;

public class OperatorToken extends AbstractTextToken {
	public OperatorToken(@NotNull String token, int srcIndex) {
		super(token, srcIndex);
	}

	@NotNull
	@Override
	public String toString() {
		return "Operator" + super.toString();
	}
}
