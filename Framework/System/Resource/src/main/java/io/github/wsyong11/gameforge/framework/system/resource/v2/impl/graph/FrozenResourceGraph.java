package io.github.wsyong11.gameforge.framework.system.resource.v2.impl.graph;

import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.util.StreamUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class FrozenResourceGraph implements ResourceGraph {
	private final Map<ResourcePath, Resource> resourceMap;
	private final Map<ResourcePath, List<ResourcePath>> treeMap;

	public FrozenResourceGraph(
		@NotNull Map<ResourcePath, Resource> resourceMap,
		@NotNull Map<ResourcePath, ? extends Collection<ResourcePath>> treeMap
	) {
		Objects.requireNonNull(resourceMap, "resourceMap is null");

		this.resourceMap = Map.copyOf(resourceMap);
		this.treeMap = Map.copyOf(treeMap
			.entrySet()
			.stream()
			.map(StreamUtils.entryValueMap(List::copyOf))
			.collect(StreamUtils.collectUnmodifiableMap()));
	}

//	@Override
//	public boolean isFrozen() {
//		return true;
//	}

	@Nullable
	@Override
	public Resource put(@NotNull ResourcePath path, @NotNull Resource resource) {
		throw new UnsupportedOperationException();
	}

	@Nullable
	@Override
	public Resource get(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		return this.resourceMap.get(path.toFile());
	}

	@Override
	public boolean remove(@NotNull ResourcePath path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean exist(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (path.isRoot())
			return true; // 根目录一定存在

		return this.resourceMap.containsKey(path.toFile())
		       || this.treeMap.containsKey(path.toDirectory());
	}

	@Override
	public boolean isDir(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		return this.treeMap.containsKey(path.toDirectory());
	}

	@Override
	public boolean isEntry(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		return this.resourceMap.containsKey(path.toFile());
	}

	@Nullable
	@Unmodifiable
	@Override
	public List<ResourcePath> list(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		List<ResourcePath> children = this.treeMap.get(path.toDirectory());
		if (children == null)
			return path.isRoot() ? List.of() : null;

		return children;
	}
}

