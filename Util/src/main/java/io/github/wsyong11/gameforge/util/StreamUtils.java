package io.github.wsyong11.gameforge.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@UtilityClass
public class StreamUtils {
	@NotNull
	public static <T> Predicate<T> distinctByKey(@NotNull Function<? super T, ?> keyExtractor) {
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

	@NotNull
	public static UnaryOperator<String> wrapStartText(@NotNull String startText) {
		Objects.requireNonNull(startText, "startText is null");
		return text -> startText + text;
	}

	@NotNull
	public static UnaryOperator<String> wrapEndText(@NotNull String endText) {
		Objects.requireNonNull(endText, "endText is null");
		return text -> text + endText;
	}

	@NotNull
	public static UnaryOperator<String> wrapText(@NotNull String startText, @NotNull String endText) {
		Objects.requireNonNull(startText, "startText is null");
		Objects.requireNonNull(endText, "endText is null");
		return text -> startText + text + endText;
	}
}
