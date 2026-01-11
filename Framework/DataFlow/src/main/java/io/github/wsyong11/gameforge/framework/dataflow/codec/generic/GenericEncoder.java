package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import io.github.wsyong11.gameforge.framework.dataflow.codec.CodecContext;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface GenericEncoder<T> {
	@NotNull
	Element encode(@NotNull CodecContext ctx, @NotNull T value, @NotNull ResolvedParameterType types)throws CodecException;
}
