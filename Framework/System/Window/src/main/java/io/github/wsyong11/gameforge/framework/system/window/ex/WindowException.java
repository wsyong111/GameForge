package io.github.wsyong11.gameforge.framework.system.window.ex;

import org.jetbrains.annotations.Nullable;

public class WindowException extends Exception {
	public WindowException() {
	}

	public WindowException(@Nullable String message) {
		super(message);
	}

	public WindowException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public WindowException(@Nullable Throwable cause) {
		super(cause);
	}
}
