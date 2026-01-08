package io.github.wsyong11.gameforge.framework.dataflow.element.mutable;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.ObjectElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.internal.AbstractObjectElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class MutableObjectElement extends AbstractObjectElement<MutableElement> implements MutableElement {
	public MutableObjectElement() {
		super(new LinkedHashMap<>());
	}

	public MutableObjectElement(@NotNull Map<String, MutableElement> map) {
		super(new LinkedHashMap<>(Objects.requireNonNull(map, "map is null")));
	}

	@NotNull
	public MutableObjectElement add(@NotNull String key, @Nullable MutableElement element) {
		Objects.requireNonNull(key, "key is null");
		this.map.put(key, element);
		return this;
	}

	@NotNull
	public MutableObjectElement clear() {
		this.map.clear();
		return this;
	}

	@NotNull
	public MutableObjectElement remove(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		this.map.remove(key);
		return this;
	}

	@NotNull
	@Override
	public ObjectElement asElement() {
		Map<String, Element> result = new LinkedHashMap<>(this.map.size());
		this.map.forEach((key, value) ->
			result.put(key, MutableElement.asElementSafe(value)));

		return new ObjectElement(result);
	}

	@NotNull
	@Override
	public MutableObjectElement copy() {
		return new MutableObjectElement(this.map);
	}

	@NotNull
	@Override
	public MutableObjectElement deepCopy() {
		Map<String, MutableElement> result = new LinkedHashMap<>(this.map.size());
		this.map.forEach((key, value) ->
			result.put(key, value.deepCopy()));

		return new MutableObjectElement(result);
	}
}
