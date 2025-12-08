package io.github.wsyong11.gameforge.framework.dataflow.element.mutable;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.NullElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.base.BaseElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public sealed interface MutableElement extends BaseElement permits MutableArrayElement, MutableBooleanElement, MutableNumberElement, MutableObjectElement, MutableStringElement {
	@NotNull
	static MutableArrayElement array() {
		return new MutableArrayElement();
	}

	@NotNull
	static MutableArrayElement array(@NotNull Collection<MutableElement> elements) {
		Objects.requireNonNull(elements, "elements is null");
		return new MutableArrayElement(elements);
	}

	@NotNull
	static MutableArrayElement array(@NotNull MutableElement... elements) {
		Objects.requireNonNull(elements, "elements is null");
		return new MutableArrayElement(List.of(elements));
	}

	@NotNull
	static MutableObjectElement object() {
		return new MutableObjectElement();
	}

	@NotNull
	static MutableObjectElement object(@NotNull Map<String, MutableElement> map) {
		Objects.requireNonNull(map, "map is null");
		return new MutableObjectElement(map);
	}

	@NotNull
	static MutableObjectElement object(@NotNull Object... pairs) {
		Objects.requireNonNull(pairs, "pairs is null");

		if (pairs.length % 2 != 0)
			throw new IllegalArgumentException("object() expects even number of arguments: key, value, key, value...");

		Map<String, MutableElement> map = new LinkedHashMap<>();

		for (int i = 0; i < pairs.length; i += 2) {
			Object keyRaw = pairs[i];
			Object valueRaw = pairs[i + 1];

			if (!(keyRaw instanceof String key))
				throw new IllegalArgumentException("key must be a String but got: " + keyRaw);

			if (!(valueRaw instanceof MutableElement value))
				throw new IllegalArgumentException("value must be an Element but got: " + valueRaw);

			map.put(key, value);
		}

		return new MutableObjectElement(map);
	}

	@NotNull
	static MutableStringElement string() {
		return new MutableStringElement("");
	}

	@NotNull
	static MutableStringElement string(@NotNull String value) {
		Objects.requireNonNull(value, "value is null");
		return new MutableStringElement(value);
	}

	@NotNull
	static MutableBooleanElement bool() {
		return new MutableBooleanElement(false);
	}

	@NotNull
	static MutableBooleanElement bool(boolean value) {
		return new MutableBooleanElement(value);
	}

	@NotNull
	static MutableNumberElement number() {
		return new MutableNumberElement(0);
	}

	@NotNull
	static MutableNumberElement number(@NotNull Number value) {
		Objects.requireNonNull(value, "value is null");
		return new MutableNumberElement(value);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	static Element asElementSafe(@Nullable MutableElement element) {
		return element == null ? NullElement.INSTANCE : element.asElement();
	}

	@NotNull
	Element asElement();
}
