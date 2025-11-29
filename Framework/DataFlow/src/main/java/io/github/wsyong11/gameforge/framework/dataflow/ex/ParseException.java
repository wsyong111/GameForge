package io.github.wsyong11.gameforge.framework.dataflow.ex;

import org.jetbrains.annotations.Nullable;

public class ParseException extends ElementCodecException {
	public ParseException() {
	}

	public ParseException(@Nullable String message) {
		super(message);
	}

	public ParseException(@Nullable String message,@Nullable Throwable cause) {
		super(message, cause);
	}

	public ParseException(@Nullable Throwable cause) {
		super(cause);
	}
}
