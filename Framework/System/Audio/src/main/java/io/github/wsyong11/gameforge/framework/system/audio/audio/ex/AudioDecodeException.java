package io.github.wsyong11.gameforge.framework.system.audio.audio.ex;

import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.Nullable;

public class AudioDecodeException extends CodecException {
	public AudioDecodeException() {
	}

	public AudioDecodeException(@Nullable String message) {
		super(message);
	}

	public AudioDecodeException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public AudioDecodeException(@Nullable Throwable cause) {
		super(cause);
	}
}
