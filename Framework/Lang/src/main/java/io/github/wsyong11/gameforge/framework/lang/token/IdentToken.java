package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;

public class IdentToken extends AbstractTextToken{
	public IdentToken(@NotNull String token, int srcIndex) {
		super(token, srcIndex);
	}

	@NotNull
	@Override
	public String toString() {
		return "Ident" + super.toString();
	}
}
