package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Token {
	@NotNull
	String getToken();

	int length();

	int getSrcIndex();

	@NotNull
	String toString();

	boolean equals(@Nullable Object o);

	int hashCode();
}
