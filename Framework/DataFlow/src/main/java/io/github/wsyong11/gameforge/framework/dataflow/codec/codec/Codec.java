package io.github.wsyong11.gameforge.framework.dataflow.codec.codec;

import io.github.wsyong11.gameforge.framework.dataflow.codec.CodecContext;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface Codec<T> {
	@NotNull
	static <V> CodecBuilder<V> builder() {
		return new CodecBuilder<>();
	}

	@NotNull
	Element encode(@NotNull CodecContext ctx, @NotNull T value, @NotNull Class<? extends T> type) throws CodecException;

	@NotNull
	T decode(@NotNull CodecContext ctx, @NotNull Element element, @NotNull Class<? extends T> type) throws CodecException;


	@NotNull
	@Unmodifiable
	Set<Class<?>> getSupportTypes();

	boolean isSupportType(@NotNull Class<?> type);

	boolean isSupportValue(@NotNull CodecContext ctx, @NotNull Object value, @NotNull Class<? extends T> type);

	boolean isSupportElement(@NotNull CodecContext ctx, @NotNull Element element, @NotNull Class<? extends T> type);
}
