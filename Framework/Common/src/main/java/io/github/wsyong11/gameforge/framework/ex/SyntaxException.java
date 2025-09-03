package io.github.wsyong11.gameforge.framework.ex;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SyntaxException extends RuntimeException {
	public SyntaxException(@NotNull String message) {
		super(message);
	}

	public SyntaxException(@NotNull String message, @NotNull Throwable e) {
		super(message, e);
	}

	public SyntaxException(@NotNull String message, int index) {
		this("SyntaxException: #" + index + ": " + message);
	}

	public SyntaxException(@NotNull String message, int index, @NotNull String text) {
		this(message + generationLocation(index, text), index);
	}

	public SyntaxException(@NotNull String message, int index, int length, @NotNull String text) {
		this(message + generationLocation(index, length, text), index);
	}

	@NotNull
	private static String generationLocation(int index, @NotNull String text) {
		Objects.requireNonNull(text, "text is null");
		return "\n| " + text + "\n| " + ("~".repeat(index)) + "^" + ("~".repeat(text.length() - index - 1));
	}

	@NotNull
	private static String generationLocation(int index, int length, @NotNull String text) {
		Objects.requireNonNull(text, "text is null");
		return "\n| " + text + "\n| " + ("~".repeat(index)) + ("^".repeat(length)) + ("~".repeat(text.length() - index - length));
	}
}
