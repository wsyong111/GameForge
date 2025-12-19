package io.github.wsyong11.gameforge.framework.config;

import io.github.wsyong11.gameforge.framework.config.annotation.ConfigAutoGenerate;
import io.github.wsyong11.gameforge.framework.config.codec.CodecMap;
import io.github.wsyong11.gameforge.framework.config.ex.RuntimeCodecException;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.path.ElementPath;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public abstract class Configuration {
	private static final Logger LOGGER = Log.getLogger();

	@NotNull
	@io.github.wsyong11.gameforge.framework.annotation.ThreadSensitive
	public static <T extends Configuration> T create(@NotNull Class<T> type, @NotNull Element element) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(element, "element is null");

		if (!Configuration.class.isAssignableFrom(type))
			throw new IllegalArgumentException("Type " + type + " is not extends Configuration");

		T config;

		int modifiers = type.getModifiers();
		if (Modifier.isAbstract(modifiers)) {
			if (!type.isAnnotationPresent(ConfigAutoGenerate.class))
				throw new IllegalArgumentException(type + "com.example.Test is an abstract class, but it is not annotated with " + ConfigAutoGenerate.class);

			Class<? extends T> impl = findImpl(type.getClassLoader(), type);
			if (impl == null)
				throw new IllegalStateException("Cannot find implement class from " + type);

			if (!type.isAssignableFrom(impl))
				throw new ClassCastException("Cannot cast implement class " + impl + " as " + type);

			config = newInstance(impl);
		} else {
			throw new UnsupportedOperationException();
		}

		config.setElement(element);

		return config;
	}

	@NotNull
	private static <T extends Configuration> T newInstance(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		Constructor<T> constructor;
		try {
			constructor = type.getConstructor();
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Cannot find constructor from " + type, e);
		}

		try {
			return constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("Cannot instantiate " + type, e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException("Cannot instantiate " + type, e.getTargetException());
		}
	}

	@SuppressWarnings("unchecked")
	@Nullable
	private static <T extends Configuration> Class<? extends T> findImpl(@NotNull ClassLoader loader, @NotNull Class<T> type) {
		Objects.requireNonNull(loader, "loader is null");
		Objects.requireNonNull(type, "type is null");

		String name = type.getName();

		try {
			return (Class<? extends T>) Class.forName(name + "$$Impl", true, loader);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	private final CodecMap codecMap;

	private volatile Element element;

	protected Configuration() {
		this.codecMap = new CodecMap();
		this.registerCodec(this.codecMap);

		this.element = null;
	}

	protected Configuration(@NotNull Element element) {
		this();
		Objects.requireNonNull(element, "element is null");

		this.setElement(element);
	}

	protected void setElement(@NotNull Element element) {
		Objects.requireNonNull(element, "element is null");

		if (this.element != null)
			throw new IllegalStateException("Element is set");

		this.element = element;
	}

	protected void registerCodec(@NotNull CodecMap map) {
	}

	@Contract("_, _, null -> _; _, _, !null -> !null")
	@Nullable
	protected <T> T get(@NotNull ElementPath path, @NotNull Class<T> type, @Nullable T defaultValue) {
		Objects.requireNonNull(path, "path is null");
		Objects.requireNonNull(type, "type is null");

		Element value = path.matchFirst(this.element);

		if (value == null)
			return defaultValue;

		try {
			return this.codecMap.decode(value, type);
		} catch (RuntimeCodecException e) {
			LOGGER.warn("{} Failed to parse config value with path {}", this.getClass(), lazy(path), e);
			throw e;
		}
	}

	@Contract("_, _, null -> _; _, _, !null -> !null")
	@Nullable
	protected <T> List<T> getList(@NotNull ElementPath path, @NotNull Class<T> type, @Nullable Collection<T> defaultValue) {
		Objects.requireNonNull(path, "path is null");
		Objects.requireNonNull(type, "type is null");

		List<Element> values = path.match(this.element);

		int size = values.size();
		if (size == 0)
			return defaultValue == null ? null : List.copyOf(defaultValue);

		List<T> result = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			Element value = values.get(i);
			try {
				result.add(this.codecMap.decode(value, type));
			} catch (RuntimeCodecException e) {
				LOGGER.warn("{} Failed to parse config value with path {} index {}",
					this.getClass(),
					lazy(path),
					i,
					e);

				throw e;
			}
		}
		return Collections.unmodifiableList(result);
	}
}
