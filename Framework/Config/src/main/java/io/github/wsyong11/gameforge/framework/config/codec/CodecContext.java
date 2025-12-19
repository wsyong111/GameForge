package io.github.wsyong11.gameforge.framework.config.codec;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CodecContext {
	@Nullable
	<T> T decode(@NotNull Element element, @NotNull Class<T> type);

	@NotNull
	<T> Element encode(@NotNull T value, @NotNull Class<T> type);


}
