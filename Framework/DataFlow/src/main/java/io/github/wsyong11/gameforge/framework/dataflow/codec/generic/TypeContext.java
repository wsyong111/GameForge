package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface TypeContext {
	@NotNull
	Type get(@Range(from = 0, to=Integer.MAX_VALUE) int index);

	@NotNull
	Type getRaw(@Range(from = 0, to=Integer.MAX_VALUE) int index);

	int count();

	@NotNull
	ParameterizedType getFullType();
}
