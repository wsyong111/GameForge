package io.github.wsyong11.gameforge.framework.system.resource.ex;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class FileClosedException extends IOException {
	public FileClosedException() {
	}

	public FileClosedException(@Nullable String message) {
		super(message);
	}

	public FileClosedException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public FileClosedException(@Nullable Throwable cause) {
		super(cause);
	}
}
