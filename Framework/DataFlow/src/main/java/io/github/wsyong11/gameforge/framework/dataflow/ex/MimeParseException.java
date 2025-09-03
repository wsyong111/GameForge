package io.github.wsyong11.gameforge.framework.dataflow.ex;

import io.github.wsyong11.gameforge.framework.ex.SyntaxException;
import org.jetbrains.annotations.NotNull;

public class MimeParseException extends SyntaxException {
	public MimeParseException(@NotNull String message) {
		super(message);
	}

	public MimeParseException(@NotNull String message, @NotNull Throwable e) {
		super(message, e);
	}

	public MimeParseException(@NotNull String message, int index) {
		super(message, index);
	}

	public MimeParseException(@NotNull String message, int index, @NotNull String text) {
		super(message, index, text);
	}

	public MimeParseException(@NotNull String message, int index, int length, @NotNull String text) {
		super(message, index, length, text);
	}
}
