package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import com.google.common.collect.Streams;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class FlatResourcePack extends AbstractResourcePack {
	private volatile Map<ResourcePath, Resource> resourceMap;
	private volatile Map<ResourcePath, List<ResourcePath>> fileTree;
	private volatile boolean loaded;

	public FlatResourcePack() {
		this.resourceMap = null;
		this.fileTree = null;
		this.loaded = false;
	}

	@NotNull
	protected abstract Iterator<Resource> getResourceList() throws IOException;

	private void ensureLoaded() throws IOException {
		if (!this.loaded)
			throw new IOException("Resource pack is not load");
	}

	@Override
	public synchronized void refresh() throws IOException {
		this.ensureOpen();

		Map<ResourcePath, Resource> resourceMap = Streams
			.stream(this.getResourceList())
			.collect(Collectors.toUnmodifiableMap(
				Resource::getPath,
				Function.identity()
			));

		Map<ResourcePath, Set<ResourcePath>> tree = new HashMap<>();
		for (ResourcePath file : resourceMap.keySet()) {
			ResourcePath current = file;

			while (!current.isEmpty()) {
				ResourcePath parent = current.parent();
				tree.computeIfAbsent(parent, k -> new HashSet<>())
				    .add(current);

				current = parent;
			}
		}

		tree.computeIfAbsent(ResourcePath.ROOT, k -> new HashSet<>());

		this.fileTree = tree
			.entrySet()
			.stream()
			.collect(Collectors.toUnmodifiableMap(
				Map.Entry::getKey,
				e -> List.copyOf(e.getValue())
			));
		this.resourceMap = resourceMap;

		this.loaded = true;
	}

	@NotNull
	@Override
	public List<ResourcePath> list(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		this.ensureOpen();
		this.ensureLoaded();

		List<ResourcePath> children = this.fileTree.get(path.toDirectory());
		if (children == null)
			throw new FileNotFoundException(path.toString());

		return children;
	}

	@NotNull
	@Override
	public Resource get(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		this.ensureOpen();
		this.ensureLoaded();

		Resource resource = this.resourceMap.get(path);
		if (resource == null)
			throw new FileNotFoundException(path.toString());

		return resource;
	}

	@Override
	public boolean exist(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (this.isClosed())
			return false;

		if (this.loaded)
			return false;

		if (path.isRoot())
			return true; // 根目录一定存在

		return this.resourceMap.containsKey(path.toFile())
			|| this.fileTree.containsKey(path.toDirectory());
	}

	@Override
	public boolean isDirectory(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (this.isClosed())
			return false;

		if (!this.loaded)
			return false;

		return this.fileTree.containsKey(path.toDirectory());
	}

	@Override
	public boolean isFile(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (this.isClosed())
			return false;

		if (!this.loaded)
			return false;

		return this.resourceMap.containsKey(path.toFile());
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			this.fileTree = null;
			this.resourceMap = null;
			this.loaded = false;
		}
	}
}
