package io.github.wsyong11.gameforge.util.collection;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;

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

	public static <I, T> void sort(@NotNull List<I> items, @NotNull List<T> list, @NotNull BiPredicate<I, T> equalsFunction) {
		Objects.requireNonNull(items, "items is null");
		Objects.requireNonNull(list, "list is null");
		Objects.requireNonNull(equalsFunction, "equalsFunction is null");

		// 建立 list 元素索引
		Map<T, Boolean> usedMap = new IdentityHashMap<>();
		for (T t : list) {
			usedMap.put(t, false);
		}

		List<T> sorted = new ArrayList<>(list.size());

		for (I item : items) {
			for (T t : list) {
				if (!usedMap.get(t) && equalsFunction.test(item, t)) {
					sorted.add(t);
					usedMap.put(t, true);
					break;
				}
			}
		}

		for (T t : list) {
			if (!usedMap.get(t))
				sorted.add(t);
		}

		list.clear();
		list.addAll(sorted);
	}
}
