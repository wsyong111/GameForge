package io.github.wsyong11.gameforge.framework.dataflow.codec;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public interface CodecContext {
	@NotNull
	Element encode(@Nullable Object value, @NotNull Type type) throws CodecException;

	@Nullable
	Object decode(@NotNull Element element, @NotNull Type type) throws CodecException;

	@NotNull
	Type getRootType();
}
