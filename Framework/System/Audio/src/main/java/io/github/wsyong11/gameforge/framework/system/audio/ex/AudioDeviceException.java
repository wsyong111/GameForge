package io.github.wsyong11.gameforge.framework.system.audio.ex;

import org.jetbrains.annotations.Nullable;

public class AudioDeviceException extends RuntimeException {
	public AudioDeviceException() {
	}

	public AudioDeviceException(@Nullable String message) {
		super(message);
	}

	public AudioDeviceException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public AudioDeviceException(@Nullable Throwable cause) {
		super(cause);
	}
}
