package io.github.wsyong11.gameforge.framework.lang.ex;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SyntaxException extends RuntimeException {
	private final String text;
	private final int index;
	private final int length;

	public SyntaxException(@NotNull String message, @NotNull String text, int index, int length) {
		super(message);
		Objects.requireNonNull(text, "text is null");

		if (length <= 0)
			throw new IllegalArgumentException("Length cannot be zero or negative");

		if (index < 0)
			throw new IndexOutOfBoundsException(index);

		this.text = text;
		this.index = index;
		this.length = length;
	}

	@NotNull
	public String getText() {
		return this.text;
	}

	public int getIndex() {
		return this.index;
	}

	public int getLength() {
		return this.length;
	}

	@Override
	public String getMessage() {
		return "Syntax error: " + super.getMessage() + "\n"
			+ "| " + this.text + "\n"
			+ "| " + "~".repeat(this.index) + "^".repeat(this.length);
	}
}
