package io.github.wsyong11.gameforge.framework.lang.token;

import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class ErrorToken implements Token {
	@NotNull
	public static Token ofCodePoint(int codePoint, int index) {
		if (!Character.isValidCodePoint(codePoint))
			throw new IllegalArgumentException(codePoint + " is not a valid code point");

		return new CodePointErrorToken(codePoint, index);
	}
	@NotNull
	public static Token ofString(@NotNull String token, int index) {
		Objects.requireNonNull(token, "token is null");
		return new StringErrorToken(token, index);
	}

	@NotNull
	@Override
	public String toString() {
		return "Error['" + StringEscapeUtils.escapeJava(this.getToken()) + "']";
	}

	private static class CodePointErrorToken extends ErrorToken {
		private final int tokenChar;
		private final int srcIndex;

		public CodePointErrorToken(int tokenChar, int srcIndex) {
			this.tokenChar = tokenChar;
			this.srcIndex = srcIndex;
		}

		@NotNull
		@Override
		public String getToken() {
			return Character.toString(this.tokenChar);
		}

		@Override
		public int length() {
			return 1;
		}

		@Override
		public int getSrcIndex() {
			return this.srcIndex;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			CodePointErrorToken that = (CodePointErrorToken) o;
			return this.tokenChar == that.tokenChar
				&& this.srcIndex == that.srcIndex;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.tokenChar, this.srcIndex);
		}
	}

	private static class StringErrorToken extends ErrorToken {
		private final String token;
		private final int srcIndex;

		public StringErrorToken(@NotNull String token, int srcIndex) {
			Objects.requireNonNull(token, "token is null");

			this.token = token;
			this.srcIndex = srcIndex;
		}

		@NotNull
		@Override
		public String getToken() {
			return this.token;
		}

		@Override
		public int length() {
			return this.token.length();
		}

		@Override
		public int getSrcIndex() {
			return this.srcIndex;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			StringErrorToken that = (StringErrorToken) o;
			return Objects.equals(this.token, that.token)
				&& this.srcIndex == that.srcIndex;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.token, this.srcIndex);
		}
	}
}
