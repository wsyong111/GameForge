package io.github.wsyong11.gameforge.framework.env;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public interface EnvConfigItem<T> {
	@NotNull
	String getId();

	@NotNull
	Class<T> getType();

	@UnknownNullability
	T getValue();

	void setValue(@UnknownNullability T value);
}
