package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;

public class KeywordToken extends AbstractTextToken {
	public KeywordToken(@NotNull String token, int srcIndex) {
		super(token, srcIndex);
	}

	@NotNull
	@Override
	public String toString() {
		return "Keyword" + super.toString();
	}
}
