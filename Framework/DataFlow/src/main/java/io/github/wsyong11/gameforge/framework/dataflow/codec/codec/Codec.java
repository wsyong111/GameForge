package io.github.wsyong11.gameforge.framework.dataflow.codec.codec;

import io.github.wsyong11.gameforge.framework.dataflow.codec.CodecContext;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.NotNull;

public interface Codec<T> {
	@NotNull
	<V extends T> Element encode(@NotNull CodecContext ctx, @NotNull V value, @NotNull Class<V> type) throws CodecException;

	@NotNull
	<V extends T> V decode(@NotNull CodecContext ctx, @NotNull Element element, @NotNull Class<V> type) throws CodecException;

	boolean isSupportType(@NotNull Class<?> type);

	boolean isSupportValue(@NotNull CodecContext ctx, @NotNull Object value, @NotNull Class<? extends T> type);

	boolean isSupportElement(@NotNull CodecContext ctx, @NotNull Element element, @NotNull Class<? extends T> type);
}
