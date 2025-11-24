package io.github.wsyong11.gameforge.framework.dataflow;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import org.jetbrains.annotations.NotNull;

public interface ElementTree {
	@NotNull
	Element getRootElement();
}
