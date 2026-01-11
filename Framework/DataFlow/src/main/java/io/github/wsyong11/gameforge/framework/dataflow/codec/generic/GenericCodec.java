package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import io.github.wsyong11.gameforge.framework.dataflow.codec.CodecContext;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.NotNull;

public interface GenericCodec<T> {
	@NotNull
	Class<? super T> getType();

	void buildInfo(@NotNull GenericInfo.Builder<T> builder);

	@NotNull
	T decode(@NotNull CodecContext ctx, @NotNull Element element, @NotNull ResolvedParameterType types) throws CodecException;

	@NotNull
	Element encode(@NotNull CodecContext ctx, @NotNull T value, @NotNull ResolvedParameterType types) throws CodecException;

	default boolean isSupportElement(@NotNull CodecContext ctx, @NotNull Element element) {
		return true;
	}

	default boolean isSupportValue(@NotNull CodecContext ctx, @NotNull T value) {
		return true;
	}
}
