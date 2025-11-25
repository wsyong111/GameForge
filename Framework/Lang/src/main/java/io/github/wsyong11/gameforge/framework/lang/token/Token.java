package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Token {
	@NotNull
	String getToken();

	int length();

	int getSrcIndex();

	default boolean equalsToken(@NotNull String token) {
		Objects.requireNonNull(token, "token is null");
		return token.equals(this.getToken());
	}

	default boolean equalsToken(@NotNull Class<? extends Token> type) {
		Objects.requireNonNull(type, "type is null");
		return type.isInstance(this);
	}

	default boolean equalsToken(@NotNull Class<? extends Token> type, @NotNull String token) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(token, "token is null");
		return this.equalsToken(type) && this.equalsToken(token);
	}

	@NotNull
	String toString();

	boolean equals(@Nullable Object o);

	int hashCode();
}
