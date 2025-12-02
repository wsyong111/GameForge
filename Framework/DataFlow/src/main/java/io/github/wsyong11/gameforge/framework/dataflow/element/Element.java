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
	static ObjectElement object(@NotNull Object... pairs) {
		Objects.requireNonNull(pairs, "pairs is null");

		if (pairs.length % 2 != 0)
			throw new IllegalArgumentException("object() expects even number of arguments: key, value, key, value...");

		Map<String, Element> map = new LinkedHashMap<>();

		for (int i = 0; i < pairs.length; i += 2) {
			Object keyRaw = pairs[i];
			Object valueRaw = pairs[i + 1];

			if (!(keyRaw instanceof String key))
				throw new IllegalArgumentException("key must be a String but got: " + keyRaw);

			if (!(valueRaw instanceof Element value))
				throw new IllegalArgumentException("value must be an Element but got: " + valueRaw);

			map.put(key, value);
		}

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
