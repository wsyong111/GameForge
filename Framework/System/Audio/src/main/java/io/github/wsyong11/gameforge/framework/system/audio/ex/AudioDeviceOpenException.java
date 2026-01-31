package io.github.wsyong11.gameforge.framework.system.audio.ex;

import org.jetbrains.annotations.Nullable;

public class AudioDeviceOpenException extends AudioDeviceException {
	public AudioDeviceOpenException() {
	}

	public AudioDeviceOpenException(@Nullable String message) {
		super(message);
	}

	public AudioDeviceOpenException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public AudioDeviceOpenException(@Nullable Throwable cause) {
		super(cause);
	}
}
