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

/**
 * 事件总线的基本抽象类，实现了事件监听器的注册逻辑，并通过缓存提升查找监听器的速度
 */
public abstract class AbstractEventBus implements EventBus {
	/** 默认监听器缓存大小 */
	public static final int DEFAULT_CACHE_SIZE = 32;

	private final Map<IEventListener<?>, Set<Class<? extends Event>>> listenerClassMap;

	private final Map<Class<? extends Event>, List<ListenerItem<?>>> listenerMap;
	private final LoadingCache<Class<? extends Event>, List<ListenerItem<?>>> listenerCache;

	/**
	 * 使用默认缓存大小实例化
	 *
	 * @see #DEFAULT_CACHE_SIZE
	 */
	protected AbstractEventBus() {
		this(DEFAULT_CACHE_SIZE);
	}

	/**
	 * 使用自定义缓存大小实例化
	 *
	 * @param cacheSize 监听器缓存大小
	 */
	protected AbstractEventBus(int cacheSize) {
		this.listenerClassMap = new ConcurrentHashMap<>();

		this.listenerMap = new ConcurrentHashMap<>();
		this.listenerCache = CacheBuilder
			.newBuilder()
			.maximumSize(cacheSize)
			.build(new EventBusCacheLoader());
	}

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 获取事件监听器列表，不包括事件继承的监听器
	 *
	 * @param eventType 事件类型
	 * @param readOnly  是否为只读，如果为真则在监听器列表未注册时返回空的不可变列表，否则则自动创建并注册监听器列表并返回可变的列表
	 * @param <T>       事件类型
	 * @return 监听器列表
	 * @apiNote 请勿在只读的情况下修改监听器列表
	 */
	@NotNull
	protected <T extends Event> List<ListenerItem<T>> getSingleTypeListeners(@NotNull Class<T> eventType, boolean readOnly) {
		Objects.requireNonNull(eventType, "eventType is null");

		return CollectionUtils.forceCast(readOnly
			? this.listenerMap.getOrDefault(eventType, Collections.emptyList())
			: this.listenerMap.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()));
	}


	/**
	 * 获取监听器列表，将包含事件继承的监听器
	 *
	 * @param eventType 事件类型
	 * @param <T>       事件类型
	 * @return 不可变的事件监听器列表，监听器将按照其注册时的优先级按大到小排列
	 * @see #getCachedListeners(Class)
	 */
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

	/**
	 * 获取缓存的监听器列表，将包含事件继承的监听器
	 *
	 * @param eventType 事件类型
	 * @param <T>       事件类型
	 * @return 不可变的监听器列表，监听器将按照其注册时的优先级按大到小排列
	 */
	@NotNull
	@Unmodifiable
	protected <T extends Event> List<ListenerItem<T>> getCachedListeners(@NotNull Class<T> eventType) {
		Objects.requireNonNull(eventType, "eventType is null");
		return CollectionUtils.forceCast(this.listenerCache.getUnchecked(eventType));
	}

	/**
	 * 使指定的事件类型及其所有父事件类型的监听器缓存失效
	 *
	 * @param eventType 事件类型
	 */
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

	/**
	 * 用于保存监听器配置的对象
	 *
	 * @param <T> 监听器接受的事件类型
	 */
	protected static class ListenerItem<T extends Event> implements Comparable<ListenerItem<?>> {
		private final IEventListener<? super T> listener;
		private final EventPriority priority;

		/**
		 * 实例化一个监听器配置
		 *
		 * @param listener 监听器实例
		 * @param priority 监听器优先级
		 */
		public ListenerItem(@NotNull IEventListener<? super T> listener, @NotNull EventPriority priority) {
			Objects.requireNonNull(listener, "listener is null");
			Objects.requireNonNull(priority, "priority is null");

			this.listener = listener;
			this.priority = priority;
		}

		/**
		 * 获取监听器实例
		 *
		 * @return 监听器实例
		 */
		@NotNull
		public IEventListener<? super T> getListener() {
			return this.listener;
		}

		/**
		 * 获取监听器的优先级
		 *
		 * @return 监听器的优先级
		 */
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
