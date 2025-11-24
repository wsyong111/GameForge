package io.github.wsyong11.gameforge.framework.dataflow.parser;

import org.jetbrains.annotations.NotNull;

public interface ValueParser<T> {
	@NotNull
	T parse(@NotNull String value);
}
