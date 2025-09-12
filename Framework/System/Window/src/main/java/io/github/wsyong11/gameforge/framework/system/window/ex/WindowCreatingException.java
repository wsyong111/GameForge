package io.github.wsyong11.gameforge.framework.system.window.ex;

import org.jetbrains.annotations.Nullable;

public class WindowCreatingException extends WindowException {
	public WindowCreatingException() {
	}

	public WindowCreatingException(@Nullable String message) {
		super(message);
	}

	public WindowCreatingException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public WindowCreatingException(@Nullable Throwable cause) {
		super(cause);
	}
}
