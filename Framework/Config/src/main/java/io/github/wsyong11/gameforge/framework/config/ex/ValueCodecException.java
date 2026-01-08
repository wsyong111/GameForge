package io.github.wsyong11.gameforge.framework.config.ex;

import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.NotNull;

public class ValueCodecException extends CodecException {
	public ValueCodecException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}

	public ValueCodecException(@NotNull String message) {
		super(message);
	}

	public ValueCodecException(@NotNull Throwable cause) {
		super(cause);
	}
}
