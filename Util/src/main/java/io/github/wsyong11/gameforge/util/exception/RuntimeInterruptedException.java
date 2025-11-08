package io.github.wsyong11.gameforge.util.exception;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RuntimeInterruptedException extends RuntimeException {
	public RuntimeInterruptedException(@NotNull InterruptedException exception) {
		super(Objects.requireNonNull(exception, "exception is null"));
	}

	@Override
	public String getMessage() {
		return "Runtime wrapper for InterruptedException: " + this.getCause().getMessage();
	}

	@NotNull
	public InterruptedException getOriginal() {
		return (InterruptedException) this.getCause();
	}
}
