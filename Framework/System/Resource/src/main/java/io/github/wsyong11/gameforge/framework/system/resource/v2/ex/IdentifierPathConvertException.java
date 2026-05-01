package io.github.wsyong11.gameforge.framework.system.resource.v2.ex;

import org.jetbrains.annotations.Nullable;

public class IdentifierPathConvertException extends RuntimeException {
	public IdentifierPathConvertException() {
	}

	public IdentifierPathConvertException(@Nullable String message) {
		super(message);
	}

	public IdentifierPathConvertException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public IdentifierPathConvertException(@Nullable Throwable cause) {
		super(cause);
	}
}
