package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;

public class CommentToken extends Token{
	public CommentToken(@NotNull String token, int charIndex) {
		super(token, charIndex);
	}

	@NotNull
	@Override
	protected String getTypeName() {
		return "Comment";
	}
}
