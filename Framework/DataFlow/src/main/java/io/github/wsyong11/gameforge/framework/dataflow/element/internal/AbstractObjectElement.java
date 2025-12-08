package io.github.wsyong11.gameforge.framework.dataflow.element.internal;

import io.github.wsyong11.gameforge.framework.dataflow.element.base.BaseElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.base.BaseObjectElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class AbstractObjectElement<E extends BaseElement> implements BaseObjectElement<E> {
	protected final Map<String, E> map;

	public AbstractObjectElement(@NotNull Map<String, E> map) {
		Objects.requireNonNull(map, "map is null");
		this.map = map;
	}

	@Nullable
	@Override
	public E get(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.map.getOrDefault(key, null);
	}

	@Override
	public boolean contains(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.map.containsKey(key);
	}

	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	@NotNull
	@Override
	public Set<String> keys() {
		return this.map.keySet();
	}

	@NotNull
	@Override
	public Map<String, E> asMap() {
		return this.map;
	}

	public void forEachEntry(@NotNull BiConsumer<String, E> action) {
		Objects.requireNonNull(action, "action is null");
		this.map.forEach(action);
	}

	@NotNull
	@Override
	public Iterator<Map.Entry<String, E>> iterator() {
		return this.map.entrySet().iterator();
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AbstractObjectElement<?> that = (AbstractObjectElement<?>) o;
		return Objects.equals(this.map, that.map);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.map);
	}

	@NotNull
	@Override
	public String toString() {
		return "{" + this.map
			.entrySet()
			.stream()
			.map(entry -> "\"" + entry.getKey() + "\": " + entry.getValue())
			.collect(Collectors.joining(", ")) + "}";
	}
}
