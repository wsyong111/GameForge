package io.github.wsyong11.gameforge.framework.system.resource.v2.impl.graph;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.impl.query.StreamResourceQuery;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.query.ResourceQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class SimpleResourceGraph implements ResourceGraph {
	private final ListMultimap<ResourcePath, Resource> resourcesMap;
	private final Map<ResourcePath, Set<ResourcePath>> treeMap;

	private final ReadWriteLock lock;

	public SimpleResourceGraph() {
		this.resourcesMap = MultimapBuilder
			.hashKeys()
			.arrayListValues()
			.build();

		this.treeMap = new HashMap<>();

		this.lock = new ReentrantReadWriteLock();
	}

	protected SimpleResourceGraph(@NotNull ListMultimap<ResourcePath, Resource> resourcesMap, @NotNull Map<ResourcePath, Set<ResourcePath>> treeMap) {
		Objects.requireNonNull(resourcesMap, "resourcesMap is null");
		Objects.requireNonNull(treeMap, "treeMap is null");

		this.resourcesMap = MultimapBuilder
			.hashKeys()
			.arrayListValues()
			.build();

		for (Map.Entry<ResourcePath, Resource> entry : resourcesMap.entries())
			this.resourcesMap.put(entry.getKey(), entry.getValue());

		this.treeMap = treeMap
			.entrySet()
			.stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				v -> new LinkedHashSet<>(v.getValue())
			));

		this.lock = new ReentrantReadWriteLock();
	}

	@Override
	public void put(@NotNull ResourcePath path, @NotNull Resource resource) {
		Objects.requireNonNull(path, "path is null");
		Objects.requireNonNull(resource, "resource is null");

		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			boolean keyExists = this.resourcesMap.containsKey(path.toFile());
			this.resourcesMap.put(path.toFile(), resource);

			if (keyExists)
				return;

			ResourcePath currentPath = path;
			while (!currentPath.isEmpty()) {
				ResourcePath parentPath = currentPath.parent();
				this.treeMap
					.computeIfAbsent(parentPath, k -> new LinkedHashSet<>())
					.add(currentPath);

				currentPath = parentPath;
			}
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
			List<Resource> resources = this.resourcesMap.get(path.toFile());
			if (resources.isEmpty())
				return null;

			return resources.get(resources.size() - 1);
		} finally {
			lock.unlock();
		}
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<Resource> getAll(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			List<Resource> resources = this.resourcesMap.get(path.toFile());
			return List.copyOf(resources);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean sort(@NotNull ResourcePath path, @NotNull Comparator<Resource> comparator) {
		Objects.requireNonNull(path, "path is null");
		Objects.requireNonNull(comparator, "comparator is null");

		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			if (!this.resourcesMap.containsKey(path.toFile()))
				return false;

			List<Resource> resources = this.resourcesMap.get(path.toFile());
			resources.sort(comparator);
			return true;
		} finally {
			lock.unlock();
		}
	}

	private boolean removeEntry(@NotNull ResourcePath path, @Nullable Resource targetResource) {
		Objects.requireNonNull(path, "path is null");

		if (targetResource == null) {
			if (this.resourcesMap.removeAll(path.toFile()).isEmpty())
				return false;
		} else {
			if (!this.resourcesMap.remove(path.toFile(), targetResource))
				return false;
		}

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
			if (children == null)
				continue;

			for (ResourcePath child : children) {
				if (child.isDirectory()) {
					stack.push(child);
					continue;
				}

				this.resourcesMap.removeAll(child);
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
			return this.removeEntry(path.toFile(), null)
				|| this.removeDir(path.toDirectory());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean remove(@NotNull ResourcePath path, @NotNull Resource resource) {
		Objects.requireNonNull(path, "path is null");
		Objects.requireNonNull(resource, "resource is null");

		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			return this.removeEntry(path.toFile(), resource);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean exists(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (path.isRoot())
			return true; // 根目录一定存在

		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return this.resourcesMap.containsKey(path.toFile())
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
	public boolean isFile(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return this.resourcesMap.containsKey(path.toFile());
		} finally {
			lock.unlock();
		}
	}

	@Nullable
	@Unmodifiable
	@Override
	public List<ResourcePath> listChildren(@NotNull ResourcePath path) {
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

	@NotNull
	@Unmodifiable
	@Override
	public List<Resource> listAllFlat() {
		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return List.copyOf(this.resourcesMap.values());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public int size() {
		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return this.resourcesMap.size();
		} finally {
			lock.unlock();
		}
	}

	@NotNull
	@Override
	public ResourceGraph copy() {
		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return new SimpleResourceGraph(this.resourcesMap, this.treeMap);
		} finally {
			lock.unlock();
		}
	}

	@NotNull
	@Override
	public ResourceQuery query() {
		return new StreamResourceQuery(this.listAllFlat());
	}

	@Override
	public void clear() {
		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			this.resourcesMap.clear();
			this.treeMap.clear();
		} finally {
			lock.unlock();
		}
	}
}
