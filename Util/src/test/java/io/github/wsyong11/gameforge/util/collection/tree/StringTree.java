package io.github.wsyong11.gameforge.util.collection.tree;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StringTree extends Tree<String, String, String> {
	public StringTree(String rootKey) {
		super(rootKey);
	}

	@NotNull
	@Override
	protected List<String> getNodeKeys(@NotNull String key) {
		return List.of(key.split("\\."));
	}

	@NotNull
	@Override
	protected String getKey(@NotNull List<String> nodeKeys) {
		return String.join(".", nodeKeys);
	}
}