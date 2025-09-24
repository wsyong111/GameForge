package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EOFToken implements Token {
	private final int srcIndex;

	public EOFToken(int srcIndex) {
		this.srcIndex = srcIndex;
	}

	@NotNull
	@Override
	public String getToken() {
		return "<EOF>";
	}

	@Override
	public int length() {
		return 0;
	}

	@Override
	public int getSrcIndex() {
		return this.srcIndex;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EOFToken that = (EOFToken) o;
		return this.srcIndex == that.srcIndex;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.srcIndex);
	}

	@NotNull
	@Override
	public String toString() {
		return "EOF[]";
	}
}
