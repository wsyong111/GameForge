package io.github.wsyong11.gameforge.framework.event.reflect;

import io.github.wsyong11.gameforge.framework.event.Event;
import io.github.wsyong11.gameforge.framework.event.EventPriority;
import io.github.wsyong11.gameforge.framework.event.IEventListener;
import io.github.wsyong11.gameforge.framework.event.bus.AbstractEventBus;
import io.github.wsyong11.gameforge.util.collection.CollectionUtils;
import io.leangen.geantyref.GenericTypeReflector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ReflectEventBus extends AbstractEventBus {
	private static final TypeVariable<? extends Class<?>> EVENT_TYPE_PARAMETER = IEventListener.class.getTypeParameters()[0];


	// key 是注册源：如果是类注册，key = Class<?>；如果是实例注册，key = 实例对象
	private final Map<Object, List<ReflectEventListener>> listenerMap;

	protected ReflectEventBus() {
		this(DEFAULT_CACHE_SIZE);
	}

	protected ReflectEventBus(int cacheSize) {
		super(cacheSize);
		this.listenerMap = new ConcurrentHashMap<>();
	}

	private void registerInternal(@NotNull Class<?> clazz, @Nullable Object instance) {
		Objects.requireNonNull(clazz, "clazz is null");

		Object key = instance != null ? instance : clazz;

		Map<Class<? extends Event>, List<ReflectEventListener>> listeners = instance == null
			? ReflectEventListener.scanStaticMethod(clazz)
			: ReflectEventListener.scanMethod(clazz, instance);

		// FIXME: 2025/8/25 重复注册处理
		List<ReflectEventListener> reflectListeners = this.listenerMap.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>());

		for (Map.Entry<Class<? extends Event>, List<ReflectEventListener>> entry : listeners.entrySet()) {
			Class<? extends Event> type = entry.getKey();
			List<ReflectEventListener> listenerList = entry.getValue();

			reflectListeners.addAll(listenerList);

			List<ListenerItem<?>> listenerItemList = CollectionUtils.forceCast(this.getSingleTypeListeners(type, false));
			for (ReflectEventListener listener : listenerList)
				listenerItemList.add(new ListenerItem<>(listener, listener.getPriority()));

			this.invalidateCache(type);
		}
	}

	private void unregisterInternal(@NotNull Object key) {
		Objects.requireNonNull(key, "key is null");

		List<ReflectEventListener> listeners = this.listenerMap.remove(key);
		if (listeners == null)
			return;

		for (ReflectEventListener listener : listeners) {
			Class<? extends Event> eventType = listener.getEventType();

			List<ListenerItem<?>> listenerItemList = CollectionUtils.forceCast(this.getSingleTypeListeners(eventType, true));
			boolean deleted = listenerItemList.removeIf(l -> listener.equals(l.getListener()));

			if (deleted)
				this.invalidateCache(eventType);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Event> void register(@NotNull EventPriority priority, @NotNull IEventListener<T> listener) {
		Objects.requireNonNull(priority, "priority is null");
		Objects.requireNonNull(listener, "listener is null");

		Type type = GenericTypeReflector.getTypeParameter(listener.getClass(), EVENT_TYPE_PARAMETER);
		Class<T> eventType = type instanceof Class<?> ? (Class<T>) type : null;

		if (eventType == null)
			throw new IllegalArgumentException("Cannot get the event type by generic type");

		this.register(eventType, priority, listener);
	}

	@Override
	public void register(@NotNull Class<?> obj) {
		Objects.requireNonNull(obj, "obj is null");
		this.registerInternal(obj, null);
	}

	@Override
	public void register(@NotNull Object obj) {
		Objects.requireNonNull(obj, "obj is null");
		this.registerInternal(obj.getClass(), obj);
	}

	@Override
	public void unregister(@NotNull Class<?> obj) {
		Objects.requireNonNull(obj, "obj is null");
		this.unregisterInternal(obj);
	}

	@Override
	public void unregister(@NotNull Object obj) {
		Objects.requireNonNull(obj, "obj is null");
		this.unregisterInternal(obj);
	}

	@Override
	public void unregisterAll() {
		super.unregisterAll();
		this.listenerMap.clear();
	}
}
