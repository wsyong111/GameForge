package io.github.wsyong11.gameforge.framework.env;

import io.github.wsyong11.gameforge.framework.context.ServiceContext;
import io.github.wsyong11.gameforge.framework.context.annotation.UsingContext;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@UsingContext
public class EnvConfig extends ServiceContext.Instance {
	public static final Entry<Boolean> DEBUG = config("debug", Boolean.class, false);
	public static final Entry<LogLevel> LOG_LEVEL = config("logLevel", LogLevel.class, LogLevel.INFO);

	@NotNull
	public static <T> Entry<T> config(@NotNull String name, @NotNull Class<T> type) {
		return new Entry<>(name, type, null);
	}

	@NotNull
	public static <T> Entry<T> config(@NotNull String name, @NotNull Class<T> type, @Nullable T initialValue) {
		return new Entry<>(name, type, () -> initialValue);
	}

	@NotNull
	public static <T> Entry<T> config(@NotNull String name, @NotNull Class<T> type, @Nullable Supplier<T> initialValue) {
		return new Entry<>(name, type, initialValue);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@UsingContext
	@Nullable
	public static EnvConfig getInstanceUnsafe() {
		return getInstanceUnsafe(EnvConfig.class);
	}

	@UsingContext
	@NotNull
	public static EnvConfig getInstance() {
		return getInstance(EnvConfig.class);
	}

	@UsingContext
	@NotNull
	@Unmodifiable
	public static List<EnvConfig> getInstances() {
		return getInstances(EnvConfig.class);
	}

	@UsingContext
	@NotNull
	public static <T> Optional<T> getOptional(@NotNull Entry<T> config) {
		return getInstances()
			.stream()
			.filter(c -> c.contain(config))
			.map(c -> c.getValue(config))
			.findFirst()
			.flatMap(Function.identity());
	}

	@UsingContext
	@Nullable
	public static <T> T get(@NotNull Entry<T> config) {
		return getOptional(config).orElse(null);
	}

	@UsingContext
	public static boolean exists(@NotNull Entry<?> config) {
		return getInstances()
			.stream()
			.anyMatch(c -> c.contain(config));
	}

	@UsingContext
	@Nullable
	@Contract("_, _ -> param2")
	public static <T> T get(@NotNull Entry<T> config, @Nullable T defaultValue) {
		return getOptional(config).orElse(defaultValue);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private static final Object NULL = new Object();

	@NotNull
	public static Builder builder() {
		return new Builder();
	}

	private final Map<Entry<?>, Object> configs;

	public EnvConfig(@NotNull Map<Entry<?>, ?> configs) {
		Objects.requireNonNull(configs, "configs is null");

		Map<Entry<?>, Object> configMap = new HashMap<>();

		for (Map.Entry<Entry<?>, ?> entry : configs.entrySet()) {
			Entry<?> key = entry.getKey();
			Object value = entry.getValue();

			Class<?> type = key.getType();
			if (value != null && !type.isInstance(value))
				throw new IllegalArgumentException("Instance type " + type.getName() + " is not an instance of " + value);

			configMap.put(key, value == null ? NULL : value);
		}

		this.configs = Collections.unmodifiableMap(configMap);
	}

	private EnvConfig(@NotNull Map<Entry<?>, Object> configs, boolean ignoredDummy) {
		Objects.requireNonNull(configs, "configs is null");
		this.configs = Collections.unmodifiableMap(configs);
	}

	@NotNull
	public <T> Optional<T> getValue(@NotNull Entry<T> config) {
		Objects.requireNonNull(config, "config is null");

		Object value = this.configs.get(config);
		if (value == null)
			return Optional.empty();

		if (value == NULL)
			return Optional.ofNullable(config.getInitialValue());

		return Optional.of(config.getType().cast(value));
	}

	public boolean contain(@NotNull Entry<?> config) {
		Objects.requireNonNull(config, "config is null");
		return this.configs.containsKey(config);
	}

	@NotNull
	@UnmodifiableView
	public Map<Entry<?>, Object> getConfigs() {
		return this.configs;
	}

	@NotNull
	public Builder asBuilder() {
		return new Builder(this);
	}

	public static class Entry<T> {
		private final String name;
		private final Class<T> type;
		private final Supplier<T> initialValue;

		protected Entry(@NotNull String name, @NotNull Class<T> type, @Nullable Supplier<T> initialValue) {
			Objects.requireNonNull(name, "name is null");
			Objects.requireNonNull(type, "type is null");
			this.name = name;
			this.type = type;
			this.initialValue = initialValue;
		}

		@NotNull
		public String getName() {
			return this.name;
		}

		@NotNull
		public Class<T> getType() {
			return this.type;
		}

		@Nullable
		public T getInitialValue() {
			return this.initialValue == null ? null : this.initialValue.get();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Entry<?> entry = (Entry<?>) o;
			return Objects.equals(this.name, entry.name)
				&& Objects.equals(this.type, entry.type);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.name, this.type);
		}

		@Override
		public String toString() {
			return "EnvConfig.Entry<" + this.type.getName() + ">(\"" + StringEscapeUtils.escapeJava(this.name) + "\")";
		}
	}

	public static class Builder {
		private final Map<Entry<?>, Object> configs;

		public Builder() {
			this.configs = new HashMap<>();
		}

		public Builder(@NotNull EnvConfig config) {
			Objects.requireNonNull(config, "config is null");
			this.configs = new HashMap<>(config.getConfigs());
		}

		@NotNull
		public Builder item(@NotNull Entry<?> config) {
			Objects.requireNonNull(config, "config is null");
			this.configs.put(config, NULL);
			return this;
		}

		@NotNull
		public <T> Builder item(@NotNull Entry<T> config, @Nullable T value) {
			Objects.requireNonNull(config, "config is null");
			this.configs.put(config, value == null ? NULL : value);
			return this;
		}

		@NotNull
		public Builder remove(@NotNull Entry<?> config) {
			this.configs.remove(config);
			return this;
		}

		@NotNull
		public EnvConfig build() {
			return new EnvConfig(this.configs, true);
		}
	}
}
