package io.github.wsyong11.gameforge.framework.dataflow.element;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class ObjectElement implements Element, Iterable<Map.Entry<String, Element>> {
	private final Map<String, Element> map;

	public ObjectElement(@NotNull Map<String, Element> map) {
		Objects.requireNonNull(map, "map is null");
		this.map = Map.copyOf(map);
	}

	@NotNull
	public Element get(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.map.getOrDefault(key, MissingElement.INSTANCE);
	}

	public boolean contains(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.map.containsKey(key);
	}

	public int size() {
		return this.map.size();
	}

	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	@NotNull
	@UnmodifiableView
	public Set<String> keys() {
		return this.map.keySet();
	}

	@NotNull
	@UnmodifiableView
	public Map<String, Element> asMap(){
		return this.map;
	}

	@NotNull
	@Override
	public Iterator<Map.Entry<String, Element>> iterator() {
		return this.map.entrySet().iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ObjectElement that = (ObjectElement) o;
		return Objects.equals(this.map, that.map);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.map);
	}

	@Override
	public String toString() {
		return "{" + this.map
			.entrySet()
			.stream()
			.map(entry -> "\"" + entry.getKey() + "\": " + entry.getValue())
			.collect(Collectors.joining(", ")) + "}";
	}
}
