package io.github.wsyong11.gameforge.framework.system.audio.audio.ex;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class AudioDecodeIOException extends AudioDecodeException {
	public AudioDecodeIOException(@NotNull IOException cause) {
		super(cause);
	}

	@NotNull
	public IOException getIOCause() {
		return (IOException) this.getCause();
	}
}
