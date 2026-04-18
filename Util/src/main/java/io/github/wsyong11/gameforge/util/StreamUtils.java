package io.github.wsyong11.gameforge.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class StreamUtils {
	@NotNull
	public static <T> Predicate<T> distinct(@NotNull Function<? super T, ?> keyExtractor) {
		Objects.requireNonNull(keyExtractor, "keyExtractor is null");

		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	@NotNull
	public static <T> Predicate<T> distinct() {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return seen::add;
	}

	@NotNull
	public static <T, V extends T> Function<T, V> cast(@NotNull Class<V> type) {
		Objects.requireNonNull(type, "type is null");
		return type::cast;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public static UnaryOperator<String> wrapStartText(@NotNull String startText) {
		Objects.requireNonNull(startText, "startText is null");
		return text -> startText + text;
	}

	@NotNull
	public static UnaryOperator<String> wrapStartText(char startText) {
		return text -> startText + text;
	}

	@NotNull
	public static UnaryOperator<String> wrapEndText(@NotNull String endText) {
		Objects.requireNonNull(endText, "endText is null");
		return text -> text + endText;
	}

	@NotNull
	public static UnaryOperator<String> wrapEndText(char endText) {
		return text -> text + endText;
	}

	@NotNull
	public static UnaryOperator<String> wrapText(@NotNull String startText, @NotNull String endText) {
		Objects.requireNonNull(startText, "startText is null");
		Objects.requireNonNull(endText, "endText is null");
		return text -> startText + text + endText;
	}

	@NotNull
	public static UnaryOperator<String> wrapText(char startText, char endText) {
		return text -> startText + text + endText;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public static <T, K, V> Function<T, Map.Entry<K, V>> toEntry(@NotNull Function<T, K> keyMapper, @NotNull Function<T, V> valueMapper) {
		Objects.requireNonNull(keyMapper, "keyMapper is null");
		Objects.requireNonNull(valueMapper, "valueMapper is null");
		return value -> Map.entry(keyMapper.apply(value), valueMapper.apply(value));
	}

	@NotNull
	public static <K, V, VR> Function<Map.Entry<K, V>, Map.Entry<K, VR>> entryValueMap(@NotNull Function<V, VR> mapper) {
		Objects.requireNonNull(mapper, "mapper is null");
		return value -> Map.entry(value.getKey(), mapper.apply(value.getValue()));
	}

	@NotNull
	public static <K, V, KR> Function<Map.Entry<K, V>, Map.Entry<KR, V>> entryKeyMap(@NotNull Function<K, KR> mapper) {
		Objects.requireNonNull(mapper, "mapper is null");
		return value -> Map.entry(mapper.apply(value.getKey()), value.getValue());
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> collectUnmodifiableMap() {
		return Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue);
	}

	@NotNull
	public static <T> Iterable<T> toIterable(@NotNull Stream<T> stream) {
		Objects.requireNonNull(stream, "stream is null");
		return stream::iterator;
	}
}
