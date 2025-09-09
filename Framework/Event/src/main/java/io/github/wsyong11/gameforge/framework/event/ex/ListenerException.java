package io.github.wsyong11.gameforge.framework.event.ex;

import org.jetbrains.annotations.Nullable;

public class ListenerException extends RuntimeException {
	public ListenerException() {
	}

	public ListenerException(@Nullable String message) {
		super(message);
	}

	public ListenerException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public ListenerException(@Nullable Throwable cause) {
		super(cause);
	}
}
