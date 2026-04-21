package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.graph;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class SimpleResourceGraph implements ResourceGraph {
	private final Map<ResourcePath, Resource> resourceMap;
	private final Map<ResourcePath, Set<ResourcePath>> treeMap;

	private final ReadWriteLock lock;

	public SimpleResourceGraph() {
		this.resourceMap = new HashMap<>();
		this.treeMap = new HashMap<>();

		this.lock = new ReentrantReadWriteLock();
	}

	protected SimpleResourceGraph(@NotNull Map<ResourcePath, Resource> resourceMap, @NotNull Map<ResourcePath, Set<ResourcePath>> treeMap) {
		Objects.requireNonNull(resourceMap, "resourceMap is null");
		Objects.requireNonNull(treeMap, "treeMap is null");

		this.resourceMap = new HashMap<>(resourceMap);
		this.treeMap = treeMap
			.entrySet()
			.stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				v -> new LinkedHashSet<>(v.getValue())
			));

		this.lock = new ReentrantReadWriteLock();
	}

//	@Override
//	public boolean isFrozen() {
//		return false;
//	}
//
//	@NotNull
//	public ResourceGraph freeze() {
//		Map<ResourcePath, Resource> resourceMap;
//		Map<ResourcePath, List<ResourcePath>> treeMap;
//
//		Lock lock = this.lock.readLock();
//		lock.lock();
//		try {
//			resourceMap = Map.copyOf(this.resourceMap);
//			treeMap = this.treeMap
//				.entrySet()
//				.stream()
//				.map(StreamUtils.entryValueMap(List::copyOf))
//				.collect(StreamUtils.collectUnmodifiableMap());
//		} finally {
//			lock.unlock();
//		}
//
//		return new FrozenResourceGraph(
//			resourceMap,
//			treeMap
//		);
//	}

	@Nullable
	@Override
	public Resource put(@NotNull ResourcePath path, @NotNull Resource resource) {
		Objects.requireNonNull(path, "path is null");
		Objects.requireNonNull(resource, "resource is null");

		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			Resource oldResource = this.resourceMap.put(path.toFile(), resource);

			if (oldResource != null)
				return oldResource;

			ResourcePath currentPath = path;
			while (!currentPath.isEmpty()) {
				ResourcePath parentPath = currentPath.parent();
				this.treeMap
					.computeIfAbsent(parentPath, k -> new LinkedHashSet<>())
					.add(currentPath);

				currentPath = parentPath;
			}

			return null;
		} finally {
			lock.unlock();
		}
	}

	@Nullable
	@Override
	public Resource get(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return this.resourceMap.get(path.toFile());
		} finally {
			lock.unlock();
		}
	}

	private boolean removeEntry(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (this.resourceMap.remove(path.toFile()) == null)
			return false;

		ResourcePath parentPath = path.parent();
		Set<ResourcePath> parentChildren = this.treeMap.get(parentPath);
		if (parentChildren == null)
			return true;

		parentChildren.remove(path);

		ResourcePath currentPath = path;
		while (!currentPath.isRoot()) {
			Set<ResourcePath> children = this.treeMap.get(currentPath);
			if (children == null)
				break;

			if (!children.isEmpty())
				break;

			this.treeMap.remove(currentPath);
			currentPath = currentPath.parent();
		}

		return true;
	}

	private boolean removeDir(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (!this.treeMap.containsKey(path))
			return false;

		Deque<ResourcePath> stack = new ArrayDeque<>();
		stack.push(path);

		while (!stack.isEmpty()) {
			ResourcePath currentPath = stack.pop();
			Set<ResourcePath> children = this.treeMap.remove(currentPath);

			for (ResourcePath child : children) {
				if (child.isDirectory()) {
					stack.push(child);
					continue;
				}

				this.resourceMap.remove(child);
			}
		}

		ResourcePath parentPath = path.parent();
		Set<ResourcePath> parentChildren = this.treeMap.get(parentPath);
		if (parentChildren != null)
			parentChildren.remove(path);

		return true;
	}

	@Override
	public boolean remove(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			return this.removeEntry(path.toFile())
			       || this.removeDir(path.toDirectory());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean exist(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (path.isRoot())
			return true; // 根目录一定存在

		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return this.resourceMap.containsKey(path.toFile())
			       || this.treeMap.containsKey(path.toDirectory());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isDir(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return this.treeMap.containsKey(path.toDirectory());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isEntry(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return this.resourceMap.containsKey(path.toFile());
		} finally {
			lock.unlock();
		}
	}

	@Nullable
	@Unmodifiable
	@Override
	public List<ResourcePath> list(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			Set<ResourcePath> children = this.treeMap.get(path.toDirectory());
			if (children == null)
				return path.isRoot() ? List.of() : null;

			return List.copyOf(children);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public int size() {
		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return this.resourceMap.size();
		} finally {
			lock.unlock();
		}
	}

	@NotNull
	@Override
	public ResourceGraph copy() {
		return new SimpleResourceGraph(this.resourceMap, this.treeMap);
	}

	@Override
	public void clear() {
		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			this.resourceMap.clear();
			this.treeMap.clear();
		} finally {
			lock.unlock();
		}
	}
}
