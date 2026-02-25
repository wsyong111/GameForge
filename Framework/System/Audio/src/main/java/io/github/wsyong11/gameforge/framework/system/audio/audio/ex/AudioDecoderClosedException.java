package io.github.wsyong11.gameforge.framework.system.audio.audio.ex;

import org.jetbrains.annotations.Nullable;

public class AudioDecoderClosedException extends AudioDecodeException {
	public AudioDecoderClosedException() {
	}

	public AudioDecoderClosedException(@Nullable String message) {
		super(message);
	}

	public AudioDecoderClosedException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public AudioDecoderClosedException(@Nullable Throwable cause) {
		super(cause);
	}
}
