package io.github.wsyong11.gameforge.framework.config.preference;

import io.github.wsyong11.gameforge.framework.config.preference.listener.PreferenceChangedListener;
import io.github.wsyong11.gameforge.framework.dataflow.element.*;
import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

public class ElementPreferencesStore implements PreferencesStore {
	private static final Logger LOGGER = Log.getLogger();

	private final Consumer<ObjectElement> changeCallback;

	private final ListenerList listenerList;

	private final Map<String, Element> config;
	private final ReadWriteLock configLock;

	public ElementPreferencesStore(@NotNull ObjectElement root, @NotNull Consumer<ObjectElement> changeCallback) {
		Objects.requireNonNull(root, "root is null");
		Objects.requireNonNull(changeCallback, "changeCallback is null");

		this.changeCallback = changeCallback;

		this.listenerList = ListenerList.sync();

		this.config = new HashMap<>();
		this.configLock = new ReentrantReadWriteLock();

		this.scanRoot(root);
	}

	private void scanRoot(@NotNull ObjectElement root) {
		Objects.requireNonNull(root, "root is null");

		// Pair<CurrentElement, FieldList>
		Deque<Pair<ObjectElement, List<String>>> stack = new ArrayDeque<>();
		stack.push(Pair.ofNonNull(root, new ArrayList<>()));

		while (!stack.isEmpty()) {
			Map.Entry<ObjectElement, List<String>> entry = stack.pop();
			ObjectElement current = entry.getKey();
			List<String> path = entry.getValue();

			for (Map.Entry<String, Element> childEntry : current) {
				String key = childEntry.getKey();
				Element value = childEntry.getValue();

				List<String> newPath = new ArrayList<>(path);
				newPath.add(key);

				if (value instanceof ObjectElement obj) {
					stack.push(Pair.ofNonNull(obj, newPath));
				} else {
					String configKey = String.join(".", newPath);
					this.config.put(configKey, value);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void notifyChanged(@NotNull Set<String> keys) {
		Objects.requireNonNull(keys, "keys is null");

		ObjectElement result;

		Lock lock = this.configLock.readLock();
		lock.lock();
		try {
			Map<String, Object> root = new HashMap<>();

			for (Map.Entry<String, Element> entry : this.config.entrySet()) {
				String key = entry.getKey();
				Element value = entry.getValue();
				String[] fields = key.split("\\.");

				Map<String, Object> current = root;
				for (int i = 0; i < fields.length - 1; i++) {
					String field = fields[i];
					Object child = current.get(field);

					if (child instanceof Map<?, ?> map) {
						current = (Map<String, Object>) map;
					} else {
						Map<String, Object> newMap = new HashMap<>();
						current.put(field, newMap);
						current = newMap;
					}
				}

				current.put(fields[fields.length - 1], value);
			}

			result = (ObjectElement) ElementBuilder.withObject(root);
		} finally {
			lock.unlock();
		}

		this.changeCallback.accept(result);

		Set<String> keysView = Collections.unmodifiableSet(keys);

		this.listenerList.fire(
			PreferenceChangedListener.class,
			l -> l.onPreferenceChanged(this, keysView),
			ListenerExceptionCallback.log(LOGGER));
	}

	@NotNull
	@Unmodifiable
	@Override
	public Set<String> getKeys() {
		return Set.copyOf(this.config.keySet());
	}

	@Override
	public void addChangedListener(@NotNull PreferenceChangedListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.add(PreferenceChangedListener.class, listener);
	}

	@Override
	public void removeChangedListener(@NotNull PreferenceChangedListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.remove(PreferenceChangedListener.class, listener);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Nullable
	@Override
	public Boolean getBoolean(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");

		Element element = this.getConfigElement(key);
		if (element instanceof BooleanElement bool)
			return bool.getValue();

		return null;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Nullable
	private <T extends Number> T getNumber(@NotNull Element element, @NotNull Class<T> type, @NotNull Function<Number, T> numberConverter) {
		Objects.requireNonNull(element, "element is null");
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(numberConverter, "numberConverter is null");

		if (element instanceof NumberElement number)
			return numberConverter.apply(number.getNumber());

		if (element instanceof StringElement value) {
			String valueText = value.getValue();

			int length = valueText.length();
			if (length < 2)
				return null;

			char typeChar = valueText.charAt(length - 1);
			String numberText = valueText.substring(0, length - 1);

			Number result;
			try {
				result = switch (typeChar) {
					case 'b', 'B' -> Byte.parseByte(numberText);
					case 's', 'S' -> Short.parseShort(numberText);
					case 'i', 'I' -> Integer.parseInt(numberText);
					case 'l', 'L' -> Long.parseLong(numberText);
					case 'f', 'F' -> Float.parseFloat(numberText);
					case 'd', 'D' -> Double.parseDouble(numberText);
					default -> throw new IllegalArgumentException("Unknown type '" + typeChar + "'");
				};
			} catch (Exception e) {
				LOGGER.warn("Cannot parse string \"{}\" to number {}", value, type.getSimpleName(), e);
				return null;
			}

			return numberConverter.apply(result);
		}

		return null;
	}

	@Nullable
	private <T extends Number> T getNumber(@NotNull String key, @NotNull Class<T> type, @NotNull Function<Number, T> numberConverter) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(numberConverter, "numberConverter is null");

		Element element = this.getConfigElement(key);
		if (element == null)
			return null;

		return this.getNumber(element, type, numberConverter);
	}

	@Nullable
	@Override
	public Byte getByte(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.getNumber(key, Byte.class, Number::byteValue);
	}

	@Nullable
	@Override
	public Short getShort(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.getNumber(key, Short.class, Number::shortValue);
	}

	@Nullable
	@Override
	public Integer getInt(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.getNumber(key, Integer.class, Number::intValue);
	}

	@Nullable
	@Override
	public Long getLong(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.getNumber(key, Long.class, Number::longValue);
	}

	@Nullable
	@Override
	public Float getFloat(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.getNumber(key, Float.class, Number::floatValue);
	}

	@Nullable
	@Override
	public Double getDouble(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.getNumber(key, Double.class, Number::doubleValue);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Nullable
	@Override
	public String getString(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");

		Element element = this.getConfigElement(key);
		if (element instanceof StringElement string)
			return string.getValue();

		return null;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@SuppressWarnings("unchecked")
	@Nullable
	@Unmodifiable
	@Override
	public <T> List<T> getList(@NotNull String key, @NotNull Class<T> elementType) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(elementType, "elementType is null");

		Element element = this.getConfigElement(key);
		if (!(element instanceof ArrayElement array))
			return null;

		List<Element> list = array.asList();
		if (String.class.equals(elementType)) {
			if (!list.stream().allMatch(item -> item instanceof StringElement))
				return null;

			return (List<T>) list
				.stream()
				.map(item -> (StringElement) item)
				.map(StringElement::getValue)
				.toList();
		}

		if (Boolean.class.equals(elementType)) {
			if (!list.stream().allMatch(item -> item instanceof BooleanElement))
				return null;

			return (List<T>) list
				.stream()
				.map(item -> (BooleanElement) item)
				.map(BooleanElement::getValue)
				.toList();
		}

		if (Number.class.isAssignableFrom(elementType)) {
			if (!list.stream().allMatch(item -> item instanceof NumberElement || item instanceof StringElement))
				return null;

			Function<Number, Number> converter;
			if (Byte.class.equals(elementType)) converter = Number::byteValue;
			else if (Short.class.equals(elementType)) converter = Number::shortValue;
			else if (Integer.class.equals(elementType)) converter = Number::intValue;
			else if (Long.class.equals(elementType)) converter = Number::longValue;
			else if (Float.class.equals(elementType)) converter = Number::floatValue;
			else if (Double.class.equals(elementType)) converter = Number::doubleValue;
			else return null;

			return (List<T>) list
				.stream()
				.map(item -> this.getNumber(item, (Class<Number>) elementType, converter))
				.toList();
		}

		return null;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Nullable
	@Override
	public <T extends Enum<T>> T getEnum(@NotNull String key, @NotNull Class<T> type) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(type, "type is null");

		String value = this.getString(key);
		if (value == null)
			return null;

		try {
			return Enum.valueOf(type, value);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Nullable
	private Element getConfigElement(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");

		Lock lock = this.configLock.readLock();
		lock.lock();
		try {
			return this.config.get(key);
		} finally {
			lock.unlock();
		}
	}

	@NotNull
	@Override
	public Editor edit() {
		return new EditorImpl();
	}

	private class EditorImpl implements Editor {
		private final Map<String, Element> modifyCache;

		private EditorImpl() {
			this.modifyCache = new ConcurrentHashMap<>();
		}

		@NotNull
		@Override
		public Editor putBoolean(@NotNull String key, boolean value) {
			Objects.requireNonNull(key, "key is null");

			this.modifyCache.put(key, Element.bool(value));
			return this;
		}

		@NotNull
		@Override
		public Editor putByte(@NotNull String key, byte value) {
			Objects.requireNonNull(key, "key is null");

			this.modifyCache.put(key, Element.number(value));
			return this;
		}

		@NotNull
		@Override
		public Editor putShort(@NotNull String key, short value) {
			Objects.requireNonNull(key, "key is null");

			this.modifyCache.put(key, Element.number(value));
			return this;
		}

		@NotNull
		@Override
		public Editor putInt(@NotNull String key, int value) {
			Objects.requireNonNull(key, "key is null");

			this.modifyCache.put(key, Element.number(value));
			return this;
		}

		@NotNull
		@Override
		public Editor putLong(@NotNull String key, long value) {
			Objects.requireNonNull(key, "key is null");

			this.modifyCache.put(key, Element.number(value));
			return this;
		}

		@NotNull
		@Override
		public Editor putFloat(@NotNull String key, float value) {
			Objects.requireNonNull(key, "key is null");

			this.modifyCache.put(key, Element.number(value));
			return this;
		}

		@NotNull
		@Override
		public Editor putDouble(@NotNull String key, double value) {
			Objects.requireNonNull(key, "key is null");

			this.modifyCache.put(key, Element.number(value));
			return this;
		}

		@NotNull
		@Override
		public Editor putString(@NotNull String key, @NotNull String value) {
			Objects.requireNonNull(key, "key is null");
			Objects.requireNonNull(value, "value is null");

			this.modifyCache.put(key, Element.string(value));
			return this;
		}

		@NotNull
		@Override
		public <T> Editor putList(@NotNull String key, @NotNull List<T> value, @NotNull Class<T> elementType) {
			Objects.requireNonNull(key, "key is null");
			Objects.requireNonNull(value, "value is null");
			Objects.requireNonNull(elementType, "elementType is null");

			if (!Boolean.class.equals(elementType) &&
				!Byte.class.equals(elementType) &&
				!Short.class.equals(elementType) &&
				!Integer.class.equals(elementType) &&
				!Long.class.equals(elementType) &&
				!Float.class.equals(elementType) &&
				!Double.class.equals(elementType) &&
				!String.class.equals(elementType)
			)
				throw new UnsupportedOperationException("Element type " + elementType.getName() + " not support");

			List<Element> values = new ArrayList<>(value.size());
			for (T item : value) {
				if (!elementType.isInstance(item))
					throw new ClassCastException("Cannot cast " + item.getClass() + " to " + elementType);

				values.add(ElementBuilder.withObject(item));
			}

			this.modifyCache.put(key, Element.array(values));

			return this;
		}

		@NotNull
		@Override
		public <T extends Enum<T>> Editor putEnum(@NotNull String key, @NotNull T value) {
			Objects.requireNonNull(key, "key is null");
			Objects.requireNonNull(value, "value is null");

			return this.putString(key, value.name());
		}

		@NotNull
		@Override
		public Editor delete(@NotNull String key) {
			Objects.requireNonNull(key, "key is null");
			this.modifyCache.put(key, null);
			return this;
		}

		@Override
		public void commit() {
			if (this.modifyCache.isEmpty())
				return;

			Set<String> changedKeys;

			Lock writeLock = ElementPreferencesStore.this.configLock.writeLock();
			writeLock.lock();
			try {
				changedKeys = Set.copyOf(this.modifyCache.keySet());
				for (Map.Entry<String, Element> entry : this.modifyCache.entrySet()) {
					String key = entry.getKey();
					Element value = entry.getValue();

					if (value == null)
						ElementPreferencesStore.this.config.remove(key);
					else
						ElementPreferencesStore.this.config.put(key, value);
				}
				this.modifyCache.clear();
			} finally {
				writeLock.unlock();
			}

			ElementPreferencesStore.this.notifyChanged(changedKeys);
		}
	}
}
