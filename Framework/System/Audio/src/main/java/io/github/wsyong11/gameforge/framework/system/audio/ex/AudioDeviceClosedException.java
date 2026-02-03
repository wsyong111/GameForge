package io.github.wsyong11.gameforge.framework.system.audio.ex;

import org.jetbrains.annotations.Nullable;

public class AudioDeviceClosedException extends AudioDeviceException {
	public AudioDeviceClosedException() {
	}

	public AudioDeviceClosedException(@Nullable String message) {
		super(message);
	}

	public AudioDeviceClosedException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public AudioDeviceClosedException(@Nullable Throwable cause) {
		super(cause);
	}
}
