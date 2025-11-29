package io.github.wsyong11.gameforge.framework.dataflow.parser;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.ex.ElementCodecException;
import org.jetbrains.annotations.NotNull;

public interface ElementCodec {
	@NotNull
	Element decode(@NotNull String data) throws ElementCodecException;

	@NotNull
	String encode(@NotNull Element element) throws ElementCodecException;
}
