package io.github.wsyong11.gameforge.util.collection.map;

import io.github.wsyong11.gameforge.util.Wrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @param <K>
 * @param <V>
 * @deprecated Using {@link com.google.common.collect.ForwardingMap}
 */
@Deprecated
public class MapWrapper<K, V> extends Wrapper<Map<K, V>> implements Map<K, V> {
	public MapWrapper() {
	}

	public MapWrapper(@Nullable Map<K, V> delegate) {
		super(delegate);
	}

	@Override
	public int size() {
		return this.delegate().size();
	}

	@Override
	public boolean isEmpty() {
		return this.delegate().isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.delegate().containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.delegate().containsValue(value);
	}

	@Override
	public V get(Object key) {
		return this.delegate().get(key);
	}

	@Nullable
	@Override
	public V put(K key, V value) {
		return this.delegate().put(key, value);
	}

	@Override
	public V remove(Object key) {
		return this.delegate().remove(key);
	}

	@Override
	public void putAll(@NotNull Map<? extends K, ? extends V> m) {
		this.delegate().putAll(m);
	}

	@Override
	public void clear() {
		this.delegate().clear();
	}

	@NotNull
	@Override
	public Set<K> keySet() {
		return this.delegate().keySet();
	}

	@NotNull
	@Override
	public Collection<V> values() {
		return this.delegate().values();
	}

	@NotNull
	@Override
	public Set<Entry<K, V>> entrySet() {
		return this.delegate().entrySet();
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return this.delegate().getOrDefault(key, defaultValue);
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		this.delegate().forEach(action);
	}

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		this.delegate().replaceAll(function);
	}

	@Nullable
	@Override
	public V putIfAbsent(K key, V value) {
		return this.delegate().putIfAbsent(key, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return this.delegate().remove(key, value);
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return this.delegate().replace(key, oldValue, newValue);
	}

	@Nullable
	@Override
	public V replace(K key, V value) {
		return this.delegate().replace(key, value);
	}

	@Override
	public V computeIfAbsent(K key, @NotNull Function<? super K, ? extends V> mappingFunction) {
		return this.delegate().computeIfAbsent(key, mappingFunction);
	}

	@Override
	public V computeIfPresent(K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return this.delegate().computeIfPresent(key, remappingFunction);
	}

	@Override
	public V compute(K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return this.delegate().compute(key, remappingFunction);
	}

	@Override
	public V merge(K key, @NotNull V value, @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return this.delegate().merge(key, value, remappingFunction);
	}
}
