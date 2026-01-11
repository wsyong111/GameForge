package io.github.wsyong11.gameforge.framework.dataflow.codec.ex;

import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.Nullable;

public class GenericCodecException extends CodecException {
	public GenericCodecException() {
	}

	public GenericCodecException(@Nullable String message) {
		super(message);
	}

	public GenericCodecException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public GenericCodecException(@Nullable Throwable cause) {
		super(cause);
	}
}
