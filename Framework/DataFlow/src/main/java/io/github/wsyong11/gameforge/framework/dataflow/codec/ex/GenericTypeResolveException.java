package io.github.wsyong11.gameforge.framework.dataflow.codec.ex;

import org.jetbrains.annotations.Nullable;

public class GenericTypeResolveException extends GenericCodecException{
	public GenericTypeResolveException() {
	}

	public GenericTypeResolveException(@Nullable String message) {
		super(message);
	}

	public GenericTypeResolveException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public GenericTypeResolveException(@Nullable Throwable cause) {
		super(cause);
	}
}
