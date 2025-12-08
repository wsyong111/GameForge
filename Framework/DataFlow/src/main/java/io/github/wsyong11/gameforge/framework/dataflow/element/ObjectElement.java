package io.github.wsyong11.gameforge.framework.dataflow.element;

import io.github.wsyong11.gameforge.framework.dataflow.element.internal.AbstractObjectElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableObjectElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public final class ObjectElement extends AbstractObjectElement<Element> implements Element {
	public ObjectElement(@NotNull Map<String, Element> map) {
		super(Collections.unmodifiableMap(new LinkedHashMap<>(
			Objects.requireNonNull(map, "map is null")
		)));
	}

	@NotNull
	@Override
	public Element get(@NotNull String key) {
		return Objects.requireNonNullElse(super.get(key), MissingElement.INSTANCE);
	}

	@NotNull
	@UnmodifiableView
	@Override
	public Set<String> keys() {
		return super.keys();
	}

	@NotNull
	@UnmodifiableView
	@Override
	public Map<String, Element> asMap() {
		return super.asMap();
	}

	@NotNull
	@Override
	public MutableObjectElement asMutable() {
		Map<String, MutableElement> result = new LinkedHashMap<>(this.map.size());
		for (Map.Entry<String, Element> entry : this.map.entrySet()) {
			String key = entry.getKey();
			Element value = entry.getValue();
			result.put(key, value.asMutable());
		}

		return new MutableObjectElement(result);
	}
}
