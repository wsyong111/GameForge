package io.github.wsyong11.gameforge.framework.config.ex;

import org.jetbrains.annotations.NotNull;

public class RuntimeCodecException extends RuntimeException {
	public RuntimeCodecException(@NotNull String message) {
		super(message);
	}

	public RuntimeCodecException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
}
