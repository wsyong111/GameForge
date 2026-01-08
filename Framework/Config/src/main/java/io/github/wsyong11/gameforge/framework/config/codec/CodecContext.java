package io.github.wsyong11.gameforge.framework.config.codec;

import io.github.wsyong11.gameforge.framework.config.ex.ValueCodecException;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CodecContext {
	@NotNull
	<T> Element encode(@NotNull T value, @NotNull Class<T> type)throws ValueCodecException;

	@Nullable
	<T> T decode(@NotNull Element element, @NotNull Class<T> type)throws ValueCodecException;
}
