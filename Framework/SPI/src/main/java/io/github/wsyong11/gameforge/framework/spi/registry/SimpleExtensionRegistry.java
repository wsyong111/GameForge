package io.github.wsyong11.gameforge.framework.spi.registry;

import io.github.wsyong11.gameforge.framework.spi.ExtensionType;
import io.github.wsyong11.gameforge.util.IdentityRef;
import io.github.wsyong11.gameforge.util.collection.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SimpleExtensionRegistry implements ExtensionRegistry {
	private final Map<ExtensionType<?>, Set<ExtensionEntry<?>>> extensionMap;
	private final Set<IdentityRef<?>> allExtensions;

	private final ReadWriteLock lock;

	public SimpleExtensionRegistry() {
		this.extensionMap = new ConcurrentHashMap<>();
		this.allExtensions = new LinkedHashSet<>();

		this.lock = new ReentrantReadWriteLock();
	}

	@NotNull
	protected <T> Set<ExtensionEntry<T>> getExtensionSet(@NotNull ExtensionType<T> type) {
		Objects.requireNonNull(type, "type is null");
		return CollectionUtils.forceCast(this.extensionMap.computeIfAbsent(type, k -> new LinkedHashSet<>()));
	}

	@Override
	public <T> void register(@NotNull ExtensionType<T> type, @NotNull T instance) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		ExtensionEntry<T> entry = new ExtensionEntry<>(instance);

		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			Set<ExtensionEntry<T>> extensions = CollectionUtils.forceCast(
				this.extensionMap.computeIfAbsent(type,
					k -> new LinkedHashSet<>()));

			if (!extensions.add(entry))
				throw new IllegalArgumentException("Extension " + instance + " is registered");

			this.allExtensions.add(entry);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public <T> void unregister(@NotNull ExtensionType<T> type, @NotNull T instance) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		ExtensionEntry<T> entry = new ExtensionEntry<>(instance);

		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			Set<ExtensionEntry<T>> extensions = CollectionUtils.forceCast(this.extensionMap.get(type));
			if (extensions == null)
				return;

			extensions.remove(entry);
			if (extensions.isEmpty())
				this.extensionMap.remove(type);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public <T> void unregister(@NotNull T instance) {
		Objects.requireNonNull(instance, "instance is null");

		ExtensionEntry<T> entry = new ExtensionEntry<>(instance);

		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			for (Map.Entry<ExtensionType<?>, Set<ExtensionEntry<?>>> e : List.copyOf(this.extensionMap.entrySet())) {
				Set<ExtensionEntry<?>> extensions = e.getValue();
				extensions.remove(entry);

				if (extensions.isEmpty())
					this.extensionMap.remove(e.getKey());
			}
		} finally {
			lock.unlock();
		}
	}


	@Override
	public <T> boolean has(@NotNull T extension) {
		return false;
	}

	@Override
	public <T> boolean has(@NotNull ExtensionType<T> type) {
		return false;
	}

	@Override
	public <T> void setPriority(@NotNull ExtensionType<T> type, @NotNull T instance, int priority) {

	}

	@Override
	public <T> int getPriority(@NotNull ExtensionType<T> type, @NotNull T instance) {
		return 0;
	}

	@Override
	public @NotNull @Unmodifiable <T> List<T> getExtensions(@NotNull ExtensionType<T> type) {
		return List.of();
	}

	@Override
	public <T> @Nullable T getExtension(@NotNull ExtensionType<T> type) {
		return null;
	}

	@Override
	public void clear() {

	}

	@Override
	public void clear(@NotNull ExtensionType<?> type) {

	}

	protected static class ExtensionEntry<T> extends IdentityRef<T> implements Comparable<ExtensionEntry<T>> {
		private volatile int priority;

		private ExtensionEntry(@NotNull T instance) {
			super(instance);
			this.priority = 0;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}

		public int getPriority() {
			return this.priority;
		}

		@Override
		public int compareTo(@NotNull ExtensionEntry<T> o) {
			Objects.requireNonNull(o, "o is null");
			return Integer.compare(this.priority, o.priority);
		}
	}
}
