package io.github.wsyong11.gameforge.framework.lang.token;

import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractTextToken implements Token {
	private final String token;
	private final int srcIndex;

	public AbstractTextToken(@NotNull String token, int srcIndex) {
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

		AbstractTextToken that = (AbstractTextToken) o;
		return this.srcIndex == that.srcIndex
			&& Objects.equals(this.token, that.token);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.token, this.srcIndex);
	}

	@NotNull
	@Override
	public String toString() {
		return "[\"" + StringEscapeUtils.escapeJava(this.token) + "\"]";
	}
}
