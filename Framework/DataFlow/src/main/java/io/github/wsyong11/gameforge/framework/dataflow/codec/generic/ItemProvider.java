package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@FunctionalInterface
public interface ItemProvider<T> {
	@NotNull
	Collection<?> get(@NotNull T value);
}
