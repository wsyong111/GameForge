package io.github.wsyong11.gameforge.framework.dataflow.element.base;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public interface BaseObjectElement<E extends BaseElement> extends BaseElement, Iterable<Map.Entry<String, E>>  {
	E get(@NotNull String key);

	boolean contains(@NotNull String key);

	int size();

	boolean isEmpty();

	@NotNull
	Set<String> keys();

	@NotNull
	Map<String, E> asMap();
}
