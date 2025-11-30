package io.github.wsyong11.gameforge.framework.config;

import io.github.wsyong11.gameforge.framework.dataflow.element.*;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.ToDoubleBiFunction;

public class ElementConfig implements Config {
	private static final Logger LOGGER = Log.getLogger();

	private final ObjectElement root;

	private final Map<String, Element> config;

	public ElementConfig(@NotNull ObjectElement root) {
		Objects.requireNonNull(root, "root is null");

		this.root = root;

		this.config = new ConcurrentHashMap<>();

		Deque<String> keyStack = new ArrayDeque<>();
		this.scanRoot(keyStack, root);
	}

	private void scanRoot(@NotNull Deque<String> keyStack, @NotNull ObjectElement element) {
		Objects.requireNonNull(keyStack, "keyStack is null");
		Objects.requireNonNull(element, "element is null");

		for (Map.Entry<String, Element> entry : element) {
			String key = entry.getKey();
			Element value = entry.getValue();

			try {
				keyStack.addLast(key);
				if (value instanceof ObjectElement object) {
					this.scanRoot(keyStack, object);
					continue;
				}

				String configKey = String.join(".", keyStack);
				this.config.put(configKey, value);
			} finally {
				keyStack.removeLast();
			}
		}
	}

	@NotNull
	@Unmodifiable
	@Override
	public Set<String> getKeys() {
		return Set.copyOf(this.config.keySet());
	}

	// ============================================================================================================== //

	@Nullable
	@Override
	public Boolean getBoolean(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");

		Element element = this.config.get(key);
		if (element instanceof BooleanElement bool)
			return bool.getValue();

		return null;
	}

	// ============================================================================================================== //

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
			String numberText = valueText.substring(0, length - 2);

			Number result;
			try {
				result = switch (typeChar) {
					case 'b', 'B' -> Byte.parseByte(numberText);
					case 's', 'S' -> Short.parseShort(numberText);
					case 'i', 'I' -> Integer.parseInt(numberText);
					case 'l', 'L' -> Long.parseLong(numberText);
					case 'f', 'F' -> Float.parseFloat(numberText);
					case 'd', 'D' -> Double.parseDouble(numberText);
					default -> null;
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

		Element element = this.config.get(key);
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

	// ============================================================================================================== //

	@Nullable
	@Override
	public String getString(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");

		Element element = this.config.get(key);
		if (element instanceof StringElement string)
			return string.getValue();

		return null;
	}

	@Nullable
	@Unmodifiable
	@Override
	public <T> List<T> getList(@NotNull String key, @NotNull Class<T> elementType) {
		// TODO 2025/11/30:
	}

	@Nullable
	@Override
	public <T extends Enum<T>> T getEnum(@NotNull String key, @NotNull Class<T> type) {
		return null;
	}

	@Override
	public @NotNull Editor edit() {
		return null;
	}
}
