package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;

public class CommentToken extends AbstractTextToken {
	public CommentToken(@NotNull String token, int srcIndex) {
		super(token, srcIndex);
	}

	@NotNull
	@Override
	public String toString() {
		return "Comment" + super.toString();
	}
}
