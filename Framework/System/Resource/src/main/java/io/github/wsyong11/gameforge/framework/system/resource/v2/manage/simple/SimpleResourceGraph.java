package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.simple;

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

public class SimpleResourceGraph implements ResourceGraph {
	private final Map<ResourcePath, Resource> resourceMap;
	private final Map<ResourcePath, Set<ResourcePath>> treeMap;

	private final ReadWriteLock lock;

	public SimpleResourceGraph() {
		this.resourceMap = new HashMap<>();
		this.treeMap = new HashMap<>();

		this.lock = new ReentrantReadWriteLock();
	}

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

		ResourcePath parent = path.parent();
		if (parent.isRoot())
			return true;

		// TODO: 2026/4/12 Trim Dir
		return true;
	}

	private boolean removeDir(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");


	}

	@Override
	public boolean remove(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			if (this.removeEntry(path))
				return true;

			if (this.removeDir(path))
				return true;

			return false;
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
				return null;

			return List.copyOf(children);
		} finally {
			lock.unlock();
		}
	}
}
