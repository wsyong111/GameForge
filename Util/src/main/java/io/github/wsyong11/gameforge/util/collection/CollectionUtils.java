package io.github.wsyong11.gameforge.util.collection;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class CollectionUtils {
	@NotNull
	public static <T> List<T> toList(@NotNull Iterator<T> iterator) {
		Objects.requireNonNull(iterator, "iterator is null");

		List<T> items = new ArrayList<>();
		while (iterator.hasNext())
			items.add(iterator.next());
		return items;
	}

	@SuppressWarnings("unchecked")
	@Contract("null -> null; !null -> !null")
	@Nullable
	public static <T> List<T> forceCast(@Nullable List<?> list) {
		return (List<T>) list;
	}
}
