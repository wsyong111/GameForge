package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Token {
	private final String token;
	private final int charIndex;

	public Token(@NotNull String token, int charIndex) {
		Objects.requireNonNull(token, "token is null");

		this.token = token;
		this.charIndex = charIndex;
	}

	@NotNull
	public String getToken() {
		return this.token;
	}

	public int getCharIndex() {
		return this.charIndex;
	}

	@NotNull
	protected abstract String getTypeName();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Token other = (Token) o;
		return this.charIndex == other.charIndex
			&& Objects.equals(this.token, other.token);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.token, this.charIndex);
	}

	@Override
	public String toString() {
		String escaped = this.token
			.replace("\\", "\\\\")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t");

		return this.getTypeName() + "[\"" + escaped + "\"]";
	}
}
