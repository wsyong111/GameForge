package io.github.wsyong11.gameforge.framework.listener.list;

import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerException;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractListenerList implements ListenerList {
	private static final Logger LOGGER = Log.getLogger();

	private final Map<Class<? extends IListener>, List<? extends IListener>> listenerMap;

	private final AtomicInteger totalListenerCount;

	public AbstractListenerList() {
		this.listenerMap = new ConcurrentHashMap<>();

		this.totalListenerCount = new AtomicInteger(0);
	}

	protected abstract <T extends IListener> void fire(@NotNull Class<T> type, @NotNull List<T> listeners, @NotNull Consumer<T> trigger, @NotNull ListenerExceptionCallback<T> exceptionCallback);

	@Override
	public <T extends IListener> void fire(@NotNull Class<T> type, @NotNull Consumer<T> trigger, @NotNull ListenerExceptionCallback<T> exceptionCallback) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(trigger, "trigger is null");
		Objects.requireNonNull(exceptionCallback, "exceptionCallback is null");
		this.fire(type, this.getListeners(type, true), trigger, exceptionCallback);
	}

	@Override
	public <T extends IListener> void fire(@NotNull Class<T> type, @NotNull Consumer<T> trigger) throws ListenerException {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(trigger, "trigger is null");

		List<Throwable> exceptions = new ArrayList<>();
		this.fire(type, this.getListeners(type, true), trigger, (listener, exception) ->
			exceptions.add(exception));

		if (!exceptions.isEmpty()) {
			ListenerException exception = new ListenerException("Catch error in listener", exceptions.get(0));
			exceptions.forEach(exception::addSuppressed);
			throw exception;
		}
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@SuppressWarnings("unchecked")
	@NotNull
	protected <T extends IListener> List<T> getListeners(@NotNull Class<T> type, boolean getOnly) {
		Objects.requireNonNull(type, "type is null");

		List<? extends IListener> listeners;
		if (getOnly)
			listeners = this.listenerMap.getOrDefault(type, Collections.emptyList());
		else
			listeners = this.listenerMap.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>());

		return (List<T>) listeners;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public int size(@NotNull Class<? extends IListener> type) {
		Objects.requireNonNull(type, "type is null");
		return this.getListeners(type, true).size();
	}

	@Override
	public int size() {
		return this.totalListenerCount.get();
	}

	@Override
	public void clear(@NotNull Class<? extends IListener> type) {
		Objects.requireNonNull(type, "type is null");

		List<? extends IListener> listeners = this.listenerMap.remove(type);
		if (listeners == null) return;

		this.totalListenerCount.addAndGet(-listeners.size());
	}

	@Override
	public void clear() {
		this.listenerMap.clear();
		this.totalListenerCount.set(0);
	}

	@Override
	public <T extends IListener> void add(@NotNull Class<T> type, @NotNull T listener) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(listener, "listener is null");

		List<T> listeners = this.getListeners(type, false);
		listeners.add(listener);

		this.totalListenerCount.incrementAndGet();
		LOGGER.trace("Register listener {}", listener.getName());

	}

	@Override
	public <T extends IListener> void remove(@NotNull Class<T> type, @NotNull T listener) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(listener, "listener is null");

		List<T> listeners = this.getListeners(type, true);
		if (!listeners.remove(listener)) return;

		this.totalListenerCount.decrementAndGet();
		LOGGER.trace("Unregister listener {}", listener.getName());
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{" +
			"total = " + this.size() + ", " +
			"types = ["+ this.listenerMap.keySet().stream().map(Class::getSimpleName).collect(Collectors.joining(", ")) + "]}";
	}
}
