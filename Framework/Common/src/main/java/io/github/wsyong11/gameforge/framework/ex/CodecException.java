package io.github.wsyong11.gameforge.framework.ex;

import org.jetbrains.annotations.Nullable;

public class CodecException extends Exception {
	public CodecException() {
	}

	public CodecException(@Nullable String message) {
		super(message);
	}

	public CodecException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public CodecException(@Nullable Throwable cause) {
		super(cause);
	}

	protected CodecException(@Nullable String message, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
