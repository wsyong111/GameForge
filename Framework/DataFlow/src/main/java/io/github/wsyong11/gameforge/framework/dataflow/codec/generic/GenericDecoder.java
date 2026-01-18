package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import io.github.wsyong11.gameforge.framework.dataflow.codec.CodecContext;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface GenericDecoder<T> {
	@NotNull
	T decode(@NotNull CodecContext ctx, @NotNull Element element, @NotNull ResolvedType types) throws CodecException;
}
