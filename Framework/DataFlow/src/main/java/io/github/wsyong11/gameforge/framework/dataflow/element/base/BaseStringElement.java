package io.github.wsyong11.gameforge.framework.dataflow.element.base;

import org.jetbrains.annotations.NotNull;

public interface BaseStringElement<THIS extends BaseStringElement<THIS>> extends BaseElement, Comparable<THIS>{
	@NotNull
	String getValue();
}
