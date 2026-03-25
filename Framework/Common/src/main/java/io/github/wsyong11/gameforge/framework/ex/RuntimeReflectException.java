package io.github.wsyong11.gameforge.framework.ex;

import org.jetbrains.annotations.Nullable;

public class RuntimeReflectException extends RuntimeException {
	public RuntimeReflectException() {
	}

	public RuntimeReflectException(@Nullable String message) {
		super(message);
	}

	public RuntimeReflectException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public RuntimeReflectException(@Nullable Throwable cause) {
		super(cause);
	}
}
