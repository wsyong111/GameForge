package io.github.wsyong11.gameforge.framework.env;

import io.github.wsyong11.gameforge.framework.context.ServiceContext;
import io.github.wsyong11.gameforge.framework.context.annotation.UsingContext;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class EnvConfig extends ServiceContext.Instance {
	public static final Entry<Boolean> DEBUG = entry("debug", Boolean.class, false);

	@NotNull
	public static <T> Entry<T> entry(@NotNull String name, @NotNull Class<T> type) {
		return new Entry<>(name, type, null);
	}

	@NotNull
	public static <T> Entry<T> entry(@NotNull String name, @NotNull Class<T> type, @Nullable T initialValue) {
		return new Entry<>(name, type, () -> initialValue);
	}

	@NotNull
	public static <T> Entry<T> entry(@NotNull String name, @NotNull Class<T> type, @Nullable Supplier<T> initialValue) {
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
	public static <T> Optional<T> getOptional(@NotNull Entry<T> entry) {
		return getInstances()
			.stream()
			.filter(c -> c.contain(entry))
			.map(c -> c.getValue(entry))
			.findFirst()
			.flatMap(Function.identity());
	}

	@UsingContext
	@Nullable
	public static <T> T get(@NotNull Entry<T> entry) {
		return getOptional(entry).orElse(null);
	}

	@UsingContext
	public static boolean exists(@NotNull Entry<?> entry) {
		return getInstances()
			.stream()
			.anyMatch(c -> c.contain(entry));
	}

	@UsingContext
	@Nullable
	@Contract("_, null -> null; _, !null -> !null")
	public static <T> T get(@NotNull Entry<T> entry, @Nullable T defaultValue) {
		return getOptional(entry).orElse(defaultValue);
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
	public <T> Optional<T> getValue(@NotNull Entry<T> entry) {
		Objects.requireNonNull(entry, "entry is null");

		Object value = this.configs.get(entry);
		if (value == null)
			return Optional.empty();

		if (value == NULL)
			return Optional.ofNullable(entry.getInitialValue());

		return Optional.of(entry.getType().cast(value));
	}

	public boolean contain(@NotNull Entry<?> entry) {
		Objects.requireNonNull(entry, "entry is null");
		return this.configs.containsKey(entry);
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

		public Builder(@NotNull EnvConfig entry) {
			Objects.requireNonNull(entry, "entry is null");
			this.configs = new HashMap<>(entry.getConfigs());
		}

		@NotNull
		public Builder item(@NotNull Entry<?> entry) {
			Objects.requireNonNull(entry, "entry is null");
			this.configs.put(entry, NULL);
			return this;
		}

		@NotNull
		public <T> Builder item(@NotNull Entry<T> entry, @Nullable T value) {
			Objects.requireNonNull(entry, "entry is null");
			this.configs.put(entry, value == null ? NULL : value);
			return this;
		}

		@NotNull
		public Builder remove(@NotNull Entry<?> entry) {
			this.configs.remove(entry);
			return this;
		}

		@NotNull
		public EnvConfig build() {
			return new EnvConfig(this.configs, true);
		}
	}
}
