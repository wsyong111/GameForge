package io.github.wsyong11.gameforge.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Nameable {
	@NotNull
	static String getName(@Nullable Object o) {
		Objects.requireNonNull(o, "o is null");

		return o instanceof Nameable nameable ? nameable.getName() : Objects.toString(o);
	}

	@NotNull
	String getName();
}
