package io.github.wsyong11.gameforge.framework.system.resource.v2.impl.graph;

import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourcePath;
import io.github.wsyong11.gameforge.util.collection.tree.Tree;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ResourceTree<T> extends Tree<ResourcePath, T, String> {
	protected ResourceTree() {
		super("");
	}

	@NotNull
	@Override
	protected List<String> getNodeKeys(@NotNull ResourcePath key) {
		Objects.requireNonNull(key, "key is null");
		return key.toList();
	}

	@NotNull
	@Override
	protected ResourcePath getKey(@NotNull List<String> nodeKeys) {
		Objects.requireNonNull(nodeKeys, "nodeKeys is null");
		return ResourcePath.of(nodeKeys);
	}
}
