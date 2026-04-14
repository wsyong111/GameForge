package io.github.wsyong11.gameforge.framework.spi.registry;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import io.github.wsyong11.gameforge.framework.spi.ExtensionLifecycle;
import io.github.wsyong11.gameforge.framework.spi.ExtensionType;
import io.github.wsyong11.gameforge.util.IdentityRef;
import io.github.wsyong11.gameforge.util.collection.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SimpleExtensionRegistry implements ExtensionRegistry {
	private final Table<ExtensionType<?>, IdentityRef<?>, ExtensionInfo<?>> table;

	private final Map<ExtensionType<?>, List<? extends ExtensionInfo<?>>> cache;

	private final ReadWriteLock lock;

	public SimpleExtensionRegistry() {
		this.table = Tables.newCustomTable(
			new HashMap<>(),
			LinkedHashMap::new
		);

		this.cache = new ConcurrentHashMap<>();

		this.lock = new ReentrantReadWriteLock();
	}

	private <T> void invokeAttach(@NotNull ExtensionType<T> type, @NotNull T extension) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(extension, "extension is null");

		if (extension instanceof ExtensionLifecycle lifecycle)
			lifecycle.attach(type);
	}

	private <T> void invokeDetach(@NotNull ExtensionType<T> type, @NotNull T extension) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(extension, "extension is null");

		if (extension instanceof ExtensionLifecycle lifecycle)
			lifecycle.detach(type);
	}

	@NotNull
	@Unmodifiable
	private <T> List<ExtensionInfo<T>> getExtensionList(@NotNull ExtensionType<T> type) {
		Objects.requireNonNull(type, "type is null");

		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return CollectionUtils.forceCast(List.copyOf(this.table.row(type).values()));
		} finally {
			lock.unlock();
		}
	}

	@NotNull
	@UnmodifiableView
	private <T> List<ExtensionInfo<T>> getCachedExtensionList(@NotNull ExtensionType<T> type) {
		Objects.requireNonNull(type, "type is null");

		return CollectionUtils.forceCast(this.cache
			.computeIfAbsent(type, k -> this
				.getExtensionList(type)
				.stream()
				.sorted()
				.toList()));
	}

	private void invalidateCache(@NotNull ExtensionType<?> type) {
		Objects.requireNonNull(type, "type is null");
		this.cache.remove(type);
	}

	@Override
	public <T> void register(@NotNull ExtensionType<T> type, @NotNull T instance) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		IdentityRef<T> ref = IdentityRef.of(instance);

		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			if (this.table.containsColumn(ref))
				throw new IllegalArgumentException("Extension " + instance + " is registered");

			ExtensionInfo<T> info = new ExtensionInfo<>(instance);
			this.table.put(type, ref, info);

			this.invalidateCache(type);

			this.invokeAttach(type, instance);
		} finally {
			lock.unlock();
		}

	}

	@Override
	public <T> void unregister(@NotNull ExtensionType<T> type, @NotNull T instance) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		IdentityRef<T> ref = IdentityRef.of(instance);

		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			if (this.table.remove(type, ref) == null)
				return;

			Map<IdentityRef<?>, ExtensionInfo<?>> row = this.table.row(type);
			if (row.isEmpty())
				this.table.rowKeySet().remove(type);

			this.invalidateCache(type);

			this.invokeDetach(type, instance);
		} finally {
			lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void unregister(@NotNull T instance) {
		Objects.requireNonNull(instance, "instance is null");

		IdentityRef<T> ref = IdentityRef.of(instance);

		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			Map<ExtensionType<?>, ExtensionInfo<?>> refInfoMap = this.table.columnMap().remove(ref);
			if (refInfoMap == null)
				return;

			for (ExtensionType<?> type : refInfoMap.keySet()) {
				Map<IdentityRef<?>, ExtensionInfo<?>> row = this.table.row(type);
				if (row.isEmpty())
					this.table.rowKeySet().remove(type);

				this.invalidateCache(type);
				this.invokeDetach((ExtensionType<? super T>) type, instance);
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public <T> boolean has(@NotNull T extension) {
		Objects.requireNonNull(extension, "extension is null");

		IdentityRef<T> ref = IdentityRef.of(extension);

		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return this.table.containsColumn(ref);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public <T> boolean has(@NotNull ExtensionType<T> type) {
		Objects.requireNonNull(type, "type is null");

		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return this.table.containsRow(type);
		} finally {
			lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	@NotNull
	private <T> ExtensionInfo<T> getInfo(@NotNull ExtensionType<T> type, @NotNull T instance) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		ExtensionInfo<?> info = this.table.get(type, IdentityRef.of(instance));
		if (info == null)
			throw new IllegalArgumentException("Extension " + instance + " not register with the type " + type);
		return (ExtensionInfo<T>) info;
	}

	@Override
	public <T> void setPriority(@NotNull ExtensionType<T> type, @NotNull T instance, int priority) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			this.getInfo(type, instance).setPriority(priority);
			this.invalidateCache(type);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public <T> int getPriority(@NotNull ExtensionType<T> type, @NotNull T instance) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return this.getInfo(type, instance).getPriority();
		} finally {
			lock.unlock();
		}
	}

	@NotNull
	@Unmodifiable
	@Override
	public <T> List<T> getExtensions(@NotNull ExtensionType<T> type) {
		Objects.requireNonNull(type, "type is null");
		return this
			.getCachedExtensionList(type)
			.stream()
			.map(ExtensionInfo::getInstance)
			.toList();
	}

	@Nullable
	@Override
	public <T> T getExtension(@NotNull ExtensionType<T> type) {
		Objects.requireNonNull(type, "type is null");

		List<ExtensionInfo<T>> list = this.getCachedExtensionList(type);
		return list.isEmpty() ? null : list.get(0).getInstance();
	}

	@Override
	public void clear() {
		this.table.clear();
	}

	@Override
	public void clear(@NotNull ExtensionType<?> type) {
		Objects.requireNonNull(type, "type is null");
		this.table.rowKeySet().remove(type);
	}

	protected static class ExtensionInfo<T> implements Comparable<ExtensionInfo<T>> {
		private final T instance;
		private volatile int priority;

		private ExtensionInfo(@NotNull T instance) {
			Objects.requireNonNull(instance, "instance is null");

			this.instance = instance;

			this.priority = 0;
		}

		@NotNull
		public T getInstance() {
			return this.instance;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}

		public int getPriority() {
			return this.priority;
		}

		@Override
		public int compareTo(@NotNull ExtensionInfo<T> o) {
			Objects.requireNonNull(o, "o is null");
			return Integer.compare(this.priority, o.priority);
		}
	}
}
