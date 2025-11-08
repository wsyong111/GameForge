package io.github.wsyong11.gameforge.framework.env;

import io.github.wsyong11.gameforge.util.StreamUtils;
import io.github.wsyong11.gameforge.util.debug.MapDebugPrinter;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@UtilityClass
public class EnvConfig {
	private static final Map<String, ConfigItem<?>> configItems = new ConcurrentHashMap<>();

	private static boolean inited = false;

	public static final EnvConfigItem<Boolean> DEBUG = addItem("debug", false, Boolean.class);
	public static final EnvConfigItem<Boolean> PRINT_CONFIG = addItem("print_config", false, Boolean.class);

	@NotNull
	private static <T> ConfigItem<T> addItem(@NotNull String id, @Nullable T defaultValue, @NotNull Class<T> type) {
		Objects.requireNonNull(id, "id is null");
		Objects.requireNonNull(type, "type is null");

		if (!configItems.containsKey(id)) {
			if (inited)
				throw new IllegalStateException("Unable to create an item after initialization");

			ConfigItem<T> item = new ConfigItem<>(id, type);
			item.setValue(defaultValue);
			configItems.put(id, item);
			return item;
		}

		ConfigItem<?> item = configItems.get(id);
		return item.cast(type);
	}

	public static boolean isInited() {
		return inited;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static void applySystemProperties() {
		Properties properties = System.getProperties();
		for (Map.Entry<String, ConfigItem<?>> entry : configItems.entrySet()) {
			String key = entry.getKey();
			ConfigItem item = entry.getValue();

			String propertyKey = EnvConfig.class.getName() + "." + key;
			String propertyValue = properties.getProperty(propertyKey);
			if (propertyValue == null)
				continue;

			try {
				Class<?> itemType = Objects.requireNonNullElse(ClassUtils.wrapperToPrimitive(item.getType()), item.getType());
				if (itemType == boolean.class) {
					item.setValue(Boolean.valueOf(propertyValue.toLowerCase(Locale.ROOT)));
				} else if (itemType == byte.class) {
					item.setValue(Byte.valueOf(propertyValue));
				} else if (itemType == char.class) {
					item.setValue(propertyValue.isEmpty() ? 0 : propertyValue.charAt(0));
				} else if (itemType == short.class) {
					item.setValue(Short.valueOf(propertyValue));
				} else if (itemType == int.class) {
					item.setValue(Integer.valueOf(propertyValue));
				} else if (itemType == long.class) {
					item.setValue(Long.valueOf(propertyValue));
				} else if (itemType == double.class) {
					item.setValue(Double.valueOf(propertyValue));
				} else if (itemType == float.class) {
					item.setValue(Float.valueOf(propertyValue));
				} else if (itemType == String.class) {
					item.setValue(propertyValue);
				} else {
					System.err.println("Failed to apply env config " + key + ", can't find method to convert String to " + itemType.getName());
				}
			} catch (Exception e) {
				System.err.println("Failed to apply env config " + key);
				e.printStackTrace();
			}
		}
	}

	private static void init() {
		if (inited) return;

		applySystemProperties();
		invokeConfigurator();

		inited = true;

		if (PRINT_CONFIG.getValue())
			MapDebugPrinter.print(configItems, Function.identity(), item -> Objects.toString(item.getValue()));
	}

	private static void invokeConfigurator() {
		ServiceLoader<EnvConfigConfigurator> configurators = ServiceLoader.load(EnvConfigConfigurator.class, EnvConfig.class.getClassLoader());
		Iterator<EnvConfigConfigurator> iterator = configurators.iterator();
		if (!iterator.hasNext())
			return;

		EnvConfigConfigurator configurator = iterator.next();
		if (iterator.hasNext()) {
			String items = StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false)
				.map(Object::getClass)
				.map(Class::getName)
				.map(StreamUtils.wrapText("\"", "\""))
				.collect(Collectors.joining(", "));
			throw new IllegalArgumentException("Discover multiple Configurators: [" + items + "]");
		}

		configurator.initConfig();
	}

	static {
		init();
	}

	private static class ConfigItem<T> implements EnvConfigItem<T> {
		private final String id;
		private final Class<T> type;
		private T value;

		public ConfigItem(@NotNull String id, @NotNull Class<T> type) {
			Objects.requireNonNull(id, "id is null");
			this.id = id;
			this.type = type;
		}

		@NotNull
		@Override
		public String getId() {
			return this.id;
		}

		@NotNull
		@Override
		public Class<T> getType() {
			return this.type;
		}

		@UnknownNullability
		@Override
		public T getValue() {
			return this.value;
		}

		@Override
		public void setValue(@UnknownNullability T value) {
			if (inited)
				throw new IllegalStateException("Unable to modify configuration values after initialization");

			this.value = value;
		}

		@SuppressWarnings("unchecked")
		@NotNull
		public <V> ConfigItem<V> cast(@NotNull Class<V> type) {
			Objects.requireNonNull(type, "type is null");

			if (!type.isAssignableFrom(this.type))
				throw new ClassCastException("Cannot cast " + this.type + " to " + type);
			return (ConfigItem<V>) this;
		}
	}
}
