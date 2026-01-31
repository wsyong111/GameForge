package io.github.wsyong11.gameforge.framework.system.audio.ex;

import org.jetbrains.annotations.Nullable;

public class AudioContextException extends AudioDeviceException {
	public AudioContextException() {
	}

	public AudioContextException(@Nullable String message) {
		super(message);
	}

	public AudioContextException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public AudioContextException(@Nullable Throwable cause) {
		super(cause);
	}
}
