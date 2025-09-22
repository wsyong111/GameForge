package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;

public class NumberToken extends AbstractTextToken {
	public NumberToken(@NotNull String token, int srcIndex) {
		super(token, srcIndex);
	}

	@NotNull
	@Override
	public String toString() {
		return "Number" + super.toString();
	}
}
