package io.github.wsyong11.gameforge.util.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface LikeMapView<K, V> {
	@NotNull
	Map<K, V> asMap();
}
