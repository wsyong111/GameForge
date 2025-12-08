package io.github.wsyong11.gameforge.framework.dataflow.element;

import io.github.wsyong11.gameforge.framework.dataflow.element.base.BaseElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public sealed interface Element extends BaseElement permits ArrayElement, BooleanElement, MissingElement, NullElement, NumberElement, ObjectElement, StringElement {
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
	static BooleanElement bool(boolean value) {
		return BooleanElement.of(value);
	}

	@NotNull
	static NumberElement number(@NotNull Number value) {
		Objects.requireNonNull(value, "value is null");
		return new NumberElement(value);
	}

	@NotNull
	static NullElement nil() {
		return NullElement.INSTANCE;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Nullable
	MutableElement asMutable();
}

/*
interface BaseElement
| abstract class AbstractArrayElement<T extends BaseElement> implements BaseElement
| abstract class AbstractArrayElement implements BaseElement
| abstract class AbstractBooleanElement implements BaseElement
| abstract class AbstractNumberElement implements BaseElement
| abstract class AbstractObjectElement<T extends BaseElement> implements BaseElement
| abstract class AbstractStringElement implements BaseElement
|
| interface Element extends BaseElement
| | class ArrayElement extends AbstractArrayElement<Element> implements Element
| | class BooleanElement extends AbstractBooleanElement implements Element
| | class MissingElement implements Element
| | class NullElement implements Element
| | class NumberElement extends AbstractNumberElement implements Element
| | class ObjectElement extends AbstractObjectElement<Element> implements Element
| \ class StringElement extends AbstractStringElement implements Element
|
| interface MutableElement extends BaseElement
| | class MutableArrayElement extends AbstractArrayElement<MutableElement> implements MutableElement
| | class MutableBooleanElement extends AbstractBooleanElement implements MutableElement
| | class MutableNumberElement extends AbstractNumberElement implements MutableElement
| | class MutableObjectElement extends AbstractObjectElement<MutableElement> implements MutableElement
| \ class MutableStringElement extends AbstractStringElement implements MutableElement
 */