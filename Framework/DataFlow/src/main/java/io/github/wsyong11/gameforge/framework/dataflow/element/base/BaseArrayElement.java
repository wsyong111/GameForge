package io.github.wsyong11.gameforge.framework.dataflow.element.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface BaseArrayElement<E extends BaseElement> extends BaseElement, Iterable<E> {
	boolean isEmpty();

	int size();

	@Nullable
	E get(int index);

	@NotNull
	List<E> asList();
}
