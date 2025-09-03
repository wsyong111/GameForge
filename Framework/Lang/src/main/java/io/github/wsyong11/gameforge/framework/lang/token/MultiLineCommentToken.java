package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;

public class MultiLineCommentToken extends CommentToken {
	public MultiLineCommentToken(@NotNull String token, int charIndex) {
		super(token, charIndex);
	}

	@NotNull
	@Override
	protected String getTypeName() {
		return "MultiLineComment";
	}
}
