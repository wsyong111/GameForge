package io.github.wsyong11.gameforge.util.debug;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@UtilityClass
public class MapDebugPrinter {
	private static final String SPLIT_LINE = "=".repeat(60);

	public static <K, V> void print(@NotNull Map<K, V> map, @NotNull Function<K, String> keyToString, @NotNull Function<V, String> valueToString) {
		print(map, System.out, keyToString, valueToString);
	}

	public static <K, V> void print(@NotNull Map<K, V> map, @NotNull PrintStream stream, @NotNull Function<K, String> keyToString, @NotNull Function<V, String> valueToString) {
		Objects.requireNonNull(map, "map is null");

		Map<K, V> mapSnapshot = Map.copyOf(map);

		stream.println(SPLIT_LINE);

		int keyMaxWidth = mapSnapshot
			.keySet()
			.stream()
			.map(keyToString)
			.mapToInt(String::length)
			.max()
			.orElse(0);

		for (Map.Entry<K, V> entry : mapSnapshot.entrySet()) {
			K key = entry.getKey();
			V value = entry.getValue();

			String keyString = keyToString.apply(key);
			stream.print(keyString);
			stream.print(" ".repeat(keyMaxWidth - keyString.length()));
			stream.print(" = ");
			stream.println(valueToString.apply(value));
		}

		stream.println(SPLIT_LINE);
	}
}
