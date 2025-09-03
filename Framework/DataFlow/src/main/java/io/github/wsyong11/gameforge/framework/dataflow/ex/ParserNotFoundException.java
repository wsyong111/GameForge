package io.github.wsyong11.gameforge.framework.dataflow.ex;

import org.jetbrains.annotations.NotNull;

public class ParserNotFoundException extends ParserException {
	public ParserNotFoundException() {
	}

	public ParserNotFoundException(@NotNull String message) {
		super(message);
	}

	public ParserNotFoundException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}

	public ParserNotFoundException(@NotNull Throwable cause) {
		super(cause);
	}
}
