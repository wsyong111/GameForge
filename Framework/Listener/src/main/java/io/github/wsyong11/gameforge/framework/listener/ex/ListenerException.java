package io.github.wsyong11.gameforge.framework.listener.ex;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListenerException extends RuntimeException {
	public ListenerException() {
	}

	public ListenerException(@Nullable String message) {
		super(message);
	}

	public ListenerException(@Nullable Throwable cause) {
		super(cause);
	}

	public ListenerException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	@NotNull
	public List<Throwable> getExceptions() {
		return List.of(this.getSuppressed());
	}
}
