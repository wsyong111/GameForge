package io.github.wsyong11.gameforge.framework.dataflow.ex;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ParserException extends IOException {
	public ParserException() {
	}

	public ParserException(@NotNull String message) {
		super(message);
	}

	public ParserException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}

	public ParserException(@NotNull Throwable cause) {
		super(cause);
	}
}
