package io.github.wsyong11.gameforge.framework.config.preference;

import io.github.wsyong11.gameforge.framework.config.preference.ex.PreferencesCodecException;
import io.github.wsyong11.gameforge.framework.config.preference.listener.PreferenceChangedListener;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableObjectElement;
import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.exception.ExceptionHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ElementPreferenceStorage extends AbstractPreferenceStorage {
	private static final Logger LOGGER = Log.getLogger();

	private static final String VALUE_KEY = "@value";

	private static void checkKey(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		if (key.contains(VALUE_KEY))
			throw new IllegalArgumentException("Key cannot contain " + VALUE_KEY + ": " + key);
	}

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

	// -------------------------------------------------------------------------------------------------------------- //

	private void scanKeys() {
		Lock lock = this.dataLock.writeLock();
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

		checkKey(key);

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

		checkKey(key);

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

	@NotNull
	private class EditorImpl implements Editor {
		// Value == null -> Remove
		private final Map<String, Pair<Class<?>, ?>> modifiedMap;

		private volatile boolean clearPreferences;

		private EditorImpl() {
			this.modifiedMap = new HashMap<>();

			this.clearPreferences = false;
		}

		@NotNull
		@Override
		public synchronized <T> Editor setValue(@NotNull String key, @Nullable T value, @NotNull Class<T> type) {
			Objects.requireNonNull(key, "key is null");
			Objects.requireNonNull(type, "type is null");
			checkKey(key);

			this.modifiedMap.put(key, Pair.of(type, value));
			return this;
		}

		@NotNull
		@Override
		public synchronized Editor delete(@NotNull String key) {
			Objects.requireNonNull(key, "key is null");
			checkKey(key);

			this.modifiedMap.put(key, null);
			return this;
		}

		@NotNull
		@Override
		public synchronized Editor clearAll() {
			this.clearPreferences = true;
			this.modifiedMap.clear();
			return this;
		}

		@Override
		public synchronized void cancel() {
			this.clearPreferences = false;
			this.modifiedMap.clear();
		}

		private void removeStorageKey(@NotNull String key) {
			Objects.requireNonNull(key, "key is null");

			ElementPreferenceStorage storage = ElementPreferenceStorage.this;

			storage.valueElementMap.remove(key);

			MutableObjectElement current = storage.element;

			for (String path : key.split("\\.")) {
				MutableElement element = current.get(path);
				if (!(element instanceof MutableObjectElement objectElement))
					return;

				current = objectElement;
			}

			current.remove(VALUE_KEY);
		}

		@SuppressWarnings("unchecked")
		private void modifyStorageKey(
			@NotNull ExceptionHandler exceptionHandler,
			@NotNull String key,
			@Nullable Object value,
			@NotNull Class<?> type
		) {
			Objects.requireNonNull(exceptionHandler, "exceptionHandler is null");
			Objects.requireNonNull(key, "key is null");
			Objects.requireNonNull(type, "type is null");

			ElementPreferenceStorage storage = ElementPreferenceStorage.this;

			MutableObjectElement current = storage.element;

			for (String path : key.split("\\.")) {
				MutableElement element = current.get(path);
				if (!(element instanceof MutableObjectElement objectElement)) {
					MutableObjectElement newObject = MutableElement.object();
					current.add(path, newObject);
					current = newObject;
					continue;
				}

				current = objectElement;
			}

			MutableElement encodedValue;
			try {
				encodedValue = storage.encode(value, (Class<? super Object>) type).asMutable();
			} catch (Exception e) {
				exceptionHandler.accept(e);
				return;
			}

			storage.valueElementMap.put(key, encodedValue);
			current.add(VALUE_KEY, encodedValue);
		}

		@Override
		public void apply() {
			ElementPreferenceStorage storage = ElementPreferenceStorage.this;

			ExceptionHandler exceptionHandler = new ExceptionHandler();

			Lock lock = storage.dataLock.writeLock();
			lock.lock();
			try {
				if (this.clearPreferences) {
					storage.valueElementMap.clear();
					storage.element.clear();
				}

				for (Map.Entry<String, Pair<Class<?>, ?>> entry : this.modifiedMap.entrySet()) {
					String key = entry.getKey();
					Pair<Class<?>, ?> value = entry.getValue();

					if (value == null)
						this.removeStorageKey(key);
					else
						this.modifyStorageKey(exceptionHandler, key, value.getRight(), value.getLeft());
				}
			} finally {
				lock.unlock();
			}

			try {
				storage.modifyCallback.run();
			} catch (Exception e) {
				LOGGER.error("Error when calling modify callback", e);
			}

			exceptionHandler.throwException("Failed to modify some preferences", PreferencesCodecException::new);
		}
	}
}
