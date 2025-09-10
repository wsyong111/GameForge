package io.github.wsyong11.gameforge.framework.listener.list;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScopedListenerList extends ListenerListWrapper {
	private final Map<Class<? extends IListener>, List<IListener>> listeners;

	public ScopedListenerList(@NotNull ListenerList delegate) {
		super(delegate);

		this.listeners = new ConcurrentHashMap<>();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void clear(@NotNull Class<? extends IListener> type) {
		Objects.requireNonNull(type, "type is null");

		List<IListener> listenerList = this.listeners.remove(type);
		if (listenerList == null)
			return;

		for (IListener listener : listenerList)
			this.remove((Class) type, listener);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void clear() {
		for (Map.Entry<Class<? extends IListener>, List<IListener>> entry : this.listeners.entrySet()) {
			Class<? extends IListener> type = entry.getKey();
			List<IListener> listenerList = entry.getValue();

			for (IListener listener : listenerList)
				this.remove((Class) type, listener);
		}
	}

	@Override
	public <T extends IListener> void add(@NotNull Class<T> type, @NotNull T listener) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(listener, "listener is null");

		super.add(type, listener);
		this.listeners.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>())
		              .add(listener);
	}

	@Override
	public <T extends IListener> void remove(@NotNull Class<T> type, @NotNull T listener) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(listener, "listener is null");

		List<IListener> listenerList = this.listeners.getOrDefault(type, List.of());
		if (!listenerList.remove(listener))
			return;

		super.remove(type, listener);
	}
}
