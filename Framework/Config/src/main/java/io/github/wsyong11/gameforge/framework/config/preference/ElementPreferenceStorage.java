package io.github.wsyong11.gameforge.framework.config.preference;

import io.github.wsyong11.gameforge.framework.config.preference.listener.PreferenceChangedListener;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableObjectElement;
import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ElementPreferenceStorage extends AbstractPreferenceStorage {
	private static final String VALUE_KEY = "@value";
	private static final Object NULL = new Object();
	private final MutableObjectElement element;
	private final Runnable modifyCallback;
	private final ListenerList listenerList;
	// Flat map
	private final Map<String, MutableElement> valueElementMap;
	private final ReadWriteLock dataLock;

	public ElementPreferenceStorage(@NotNull MutableObjectElement element, @NotNull Runnable modifyCallback) {
		Objects.requireNonNull(element, "element is null");
		Objects.requireNonNull(modifyCallback, "modifyCallback is null");

		this.element = element;
		this.modifyCallback = modifyCallback;

		this.listenerList = ListenerList.sync();

		this.valueElementMap = new HashMap<>();

		this.dataLock = new ReentrantReadWriteLock();

		this.scanKeys();
	}

	@NotNull
	private static List<String> splitPath(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");

		List<String> path = List.of(key.split("\\."));
		if (path.contains(VALUE_KEY))
			throw new IllegalArgumentException("Key cannot contain " + VALUE_KEY + ": " + key);

		return path;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private void scanKeys() {
		Lock lock = this.dataLock.readLock();
		lock.lock();
		try {
			this.valueElementMap.clear();
			this.scanElement("", this.element);
		} finally {
			lock.unlock();
		}
	}

	private void scanElement(@NotNull String path, @NotNull MutableObjectElement element) {
		Objects.requireNonNull(path, "path is null");
		Objects.requireNonNull(element, "element is null");

		MutableElement valueElement = element.get(VALUE_KEY);
		if (valueElement != null)
			this.valueElementMap.put(path, valueElement);

		for (Map.Entry<String, MutableElement> entry : element) {
			String key = entry.getKey();
			MutableElement child = entry.getValue();
			if (key.equals(VALUE_KEY))
				continue;

			String newPath = path.isEmpty() ? key : path + "." + key;
			if (child instanceof MutableObjectElement objectElement)
				this.scanElement(newPath, objectElement);
		}
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public boolean contains(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");

		Lock lock = this.dataLock.readLock();
		lock.lock();
		try {
			return this.valueElementMap.containsKey(key);
		} finally {
			lock.unlock();
		}
	}

	@NotNull
	@Unmodifiable
	@Override
	public Set<String> getKeys() {
		Lock lock = this.dataLock.readLock();
		lock.lock();
		try {
			return Set.copyOf(this.valueElementMap.keySet());
		} finally {
			lock.unlock();
		}
	}

	@Nullable
	@Override
	public <T> T getValue(@NotNull String key, @NotNull Class<T> type) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(type, "type is null");

		Lock lock = this.dataLock.readLock();
		lock.lock();
		try {
			if (!this.valueElementMap.containsKey(key))
				return null;

			MutableElement element = this.valueElementMap.get(key);
			if (element == null)
				return null;

			return this.decode(element.asElement(), type);
		} finally {
			lock.unlock();
		}
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public void addChangeListener(@NotNull PreferenceChangedListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.add(PreferenceChangedListener.class, listener);
	}

	@Override
	public void removeChangeListener(@NotNull PreferenceChangedListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.remove(PreferenceChangedListener.class, listener);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@Override
	public Editor edit() {
		return new EditorImpl();
	}

	private class EditorImpl implements Editor {
		@Override
		public @NotNull <T> Editor setValue(@NotNull String key, @Nullable T value, @NotNull Class<T> type) {
			return null;
		}

		@Override
		public @NotNull Editor delete(@NotNull String key) {
			return null;
		}

		@Override
		public @NotNull Editor clearAll() {
			return null;
		}

		@Override
		public void cancel() {

		}

		@Override
		public void apply() {

		}
	}
}
