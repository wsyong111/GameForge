package io.github.wsyong11.gameforge.util.collection.list;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

// TODO: 2025/10/4 List transform
public class ListTransform<T, R> extends ListWrapper<R> {
	private final Function<T, R> transformer;

	public ListTransform(@NotNull List<T> list, @NotNull Function<T, R> transformer) {
		this.transformer = transformer;
	}
}
