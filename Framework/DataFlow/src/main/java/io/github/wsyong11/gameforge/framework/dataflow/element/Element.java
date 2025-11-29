package io.github.wsyong11.gameforge.framework.dataflow.element;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public interface Element {
	@NotNull
	static ArrayElement array(@NotNull Collection<Element> elements) {
		Objects.requireNonNull(elements, "elements is null");
		return new ArrayElement(elements);
	}

	@NotNull
	static ArrayElement array(@NotNull Element... elements) {
		Objects.requireNonNull(elements, "elements is null");
		return new ArrayElement(List.of(elements));
	}

	@NotNull
	static ObjectElement object(@NotNull SortedMap<String, Element> map) {
		Objects.requireNonNull(map, "map is null");
		return new ObjectElement(map);
	}

	@NotNull
	static ObjectElement object(@NotNull Map<String, Element> map) {
		Objects.requireNonNull(map, "map is null");
		return new ObjectElement(map);
	}

	@NotNull
	static StringElement string(@NotNull String value) {
		Objects.requireNonNull(value, "value is null");
		return new StringElement(value);
	}

	@NotNull
	static BooleanElement primitive(boolean value) {
		return value ? BooleanElement.TRUE : BooleanElement.FALSE;
	}

	@NotNull
	static NumberElement primitive(@NotNull Number value) {
		return new NumberElement(value);
	}

	@NotNull
	static NullElement nil() {
		return NullElement.INSTANCE;
	}
}
