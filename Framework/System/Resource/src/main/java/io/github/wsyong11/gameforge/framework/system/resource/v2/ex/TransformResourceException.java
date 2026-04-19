package io.github.wsyong11.gameforge.framework.system.resource.v2.ex;

import org.jetbrains.annotations.Nullable;

public class TransformResourceException extends RuntimeException {
	public TransformResourceException(@Nullable Throwable cause) {
		super(cause);
	}

	public TransformResourceException(@Nullable String message,@Nullable Throwable cause) {
		super(message, cause);
	}

	public TransformResourceException(@Nullable String message) {
		super(message);
	}

	public TransformResourceException() {
	}
}
