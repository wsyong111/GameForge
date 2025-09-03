package io.github.wsyong11.gameforge.framework.event.ex;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 事件派发时由事件监听器抛出异常引发
 */
public class EventDispatchException extends RuntimeException {
	public EventDispatchException() {
	}

	public EventDispatchException(@Nullable String message) {
		super(message);
	}

	public EventDispatchException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public EventDispatchException(@Nullable Throwable cause) {
		super(cause);
	}

	@NotNull
	public List<Throwable> getExceptions() {
		return List.of(this.getSuppressed());
	}
}
