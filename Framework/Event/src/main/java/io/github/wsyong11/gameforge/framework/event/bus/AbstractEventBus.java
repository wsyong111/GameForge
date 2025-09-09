package io.github.wsyong11.gameforge.framework.event.bus;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.wsyong11.gameforge.framework.event.Event;
import io.github.wsyong11.gameforge.framework.event.EventBus;
import io.github.wsyong11.gameforge.framework.event.EventPriority;
import io.github.wsyong11.gameforge.framework.event.IEventListener;
import io.github.wsyong11.gameforge.util.collection.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractEventBus implements EventBus {
	public static final int DEFAULT_CACHE_SIZE = 32;

	private final Map<IEventListener<?>, Set<Class<? extends Event>>> listenerClassMap;

	private final Map<Class<? extends Event>, List<ListenerItem<?>>> listenerMap;
	private final LoadingCache<Class<? extends Event>, List<ListenerItem<?>>> listenerCache;

	protected AbstractEventBus() {
		this(DEFAULT_CACHE_SIZE);
	}

	protected AbstractEventBus(int cacheSize) {
		this.listenerClassMap = new ConcurrentHashMap<>();

		this.listenerMap = new ConcurrentHashMap<>();
		this.listenerCache = CacheBuilder
			.newBuilder()
			.maximumSize(cacheSize)
			.build(new EventBusCacheLoader());
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	protected <T extends Event> List<ListenerItem<T>> getSingleTypeListeners(@NotNull Class<T> eventType, boolean readOnly) {
		Objects.requireNonNull(eventType, "eventType is null");

		return CollectionUtils.forceCast(readOnly
			? this.listenerMap.getOrDefault(eventType, Collections.emptyList())
			: this.listenerMap.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()));
	}


	@SuppressWarnings("unchecked")
	@NotNull
	@Unmodifiable
	protected <T extends Event> List<ListenerItem<T>> getListeners(@NotNull Class<T> eventType) {
		Objects.requireNonNull(eventType, "eventType is null");

		return Event
			.getSuperClasses(eventType)
			.stream()
			.flatMap(clazz -> this.getSingleTypeListeners(clazz, true).stream())
			.map(item -> (ListenerItem<T>) item)
			.sorted()
			.toList();
	}

	@NotNull
	@Unmodifiable
	protected <T extends Event> List<ListenerItem<T>> getCachedListeners(@NotNull Class<T> eventType) {
		Objects.requireNonNull(eventType, "eventType is null");
		return CollectionUtils.forceCast(this.listenerCache.getUnchecked(eventType));
	}

	protected void invalidateCache(@NotNull Class<? extends Event> eventType) {
		Objects.requireNonNull(eventType, "eventType is null");

		for (Class<? extends Event> type : Event.getSuperClasses(eventType))
			this.listenerCache.invalidate(type);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public <T extends Event> void register(@NotNull Class<T> type, @NotNull EventPriority priority, @NotNull IEventListener<? super T> listener) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(priority, "priority is null");
		Objects.requireNonNull(listener, "listener is null");

		this.getSingleTypeListeners(type, false)
		    .add(new ListenerItem<>(listener, priority));

		this.listenerClassMap
			.computeIfAbsent(listener, k -> ConcurrentHashMap.newKeySet())
			.add(type);

		this.invalidateCache(type);
	}

	@Override
	public <T extends Event> void register(@NotNull EventPriority priority, @NotNull IEventListener<T> listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void register(@NotNull Object obj) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void register(@NotNull Class<?> obj) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends Event> void unregister(@NotNull IEventListener<T> listener) {
		Objects.requireNonNull(listener, "listener is null");

		Set<Class<? extends Event>> eventTypes = this.listenerClassMap.remove(listener);
		if (eventTypes == null)
			return;

		for (Class<? extends Event> eventType : eventTypes) {
			this.getSingleTypeListeners(eventType, true)
			    .removeIf(l -> listener.equals(l.getListener()));

			this.invalidateCache(eventType);
		}
	}

	@Override
	public void unregister(@NotNull Object obj) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unregister(@NotNull Class<?> obj) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unregisterAll() {
		this.listenerClassMap.clear();
		this.listenerMap.clear();
		this.listenerCache.invalidateAll();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	protected static class ListenerItem<T extends Event> implements Comparable<ListenerItem<?>> {
		private final IEventListener<? super T> listener;
		private final EventPriority priority;

		public ListenerItem(@NotNull IEventListener<? super T> listener, @NotNull EventPriority priority) {
			this.listener = listener;
			this.priority = priority;
		}

		@NotNull
		public IEventListener<? super T> getListener() {
			return this.listener;
		}

		@NotNull
		public EventPriority getPriority() {
			return this.priority;
		}

		@Override
		public int compareTo(@NotNull ListenerItem<?> o) {
			Objects.requireNonNull(o, "o is null");
			return Integer.compare(o.priority.getPriority(), this.priority.getPriority());
		}

		@Override
		public String toString() {
			return "ListenerItem{priority=" + this.priority + ", listener=" + this.listener + "}";
		}
	}


	private class EventBusCacheLoader extends CacheLoader<Class<? extends Event>, List<ListenerItem<?>>> {
		@NotNull
		@Override
		public List<ListenerItem<?>> load(@NotNull Class<? extends Event> key) {
			return CollectionUtils.forceCast(getListeners(key));
		}

		@NotNull
		@Override
		public Map<Class<? extends Event>, List<ListenerItem<?>>> loadAll(@NotNull Iterable<? extends Class<? extends Event>> keys) {
			HashMap<Class<? extends Event>, List<ListenerItem<?>>> result = new HashMap<>();
			for (Class<? extends Event> key : keys)
				result.put(key, CollectionUtils.forceCast(getListeners(key)));
			return result;
		}
	}
}
