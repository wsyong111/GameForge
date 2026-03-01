package io.github.wsyong11.gameforge.framework.context;

import io.github.wsyong11.gameforge.framework.context.annotation.UsingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class ServiceContext extends Context {
	@NotNull
	public static Builder builder() {
		return new Builder();
	}

	private final Map<Class<?>, Object> instances;

	public ServiceContext(@NotNull Map<Class<?>, Object> instances, boolean debug) {
		super(debug);
		Objects.requireNonNull(instances, "instances is null");

		this.instances = Map.copyOf(instances);
		for (Map.Entry<Class<?>, Object> entry : this.instances.entrySet()) {
			Class<?> type = entry.getKey();
			Object value = entry.getValue();
			if (!type.isInstance(value))
				throw new IllegalArgumentException("Instance type " + type.getName() + " is not an instance of " + value);
		}
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T getUnsafe(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");
		return (T) this.instances.get(type);
	}

	@NotNull
	public <T> Optional<T> getOptional(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");
		return Optional.ofNullable(this.getUnsafe(type));
	}

	@NotNull
	public <T> T get(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");
		T instance = this.getUnsafe(type);
		if (instance == null)
			throw new IllegalArgumentException("Type of instance " + type.getName() + " not found");
		return instance;
	}

	public static abstract class Instance {
		@Nullable
		@UsingContext
		protected static <T extends Instance> T getInstanceUnsafe(@NotNull Class<T> type) {
			Objects.requireNonNull(type, "type is null");
			return Context
				.getEachStream()
				.map(c -> c.asUnsafe(ServiceContext.class))
				.filter(Objects::nonNull)
				.map(c -> c.getUnsafe(type))
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
		}

		@NotNull
		@UsingContext
		protected static <T extends Instance> T getInstance(@NotNull Class<T> type) {
			Objects.requireNonNull(type, "type is null");

			T instance = getInstanceUnsafe(type);
			if (instance == null)
				throw new IllegalArgumentException("Type of instance " + type.getName() + " not found");
			return instance;
		}

		@NotNull
		@UsingContext
		@Unmodifiable
		protected static <T extends Instance> List<T> getInstances(@NotNull Class<T> type) {
			Objects.requireNonNull(type, "type is null");

			return Context
				.getEachStream()
				.map(c -> c.asUnsafe(ServiceContext.class))
				.filter(Objects::nonNull)
				.map(c -> c.getUnsafe(type))
				.filter(Objects::nonNull)
				.toList();
		}
	}

	public static class Builder {
		private final Map<Class<?>, Object> instances;
		private boolean debug;

		public Builder() {
			this.instances = new HashMap<>();
			this.debug = false;
		}

		@NotNull
		public <T> Builder item(@NotNull Class<T> type, @NotNull T instance) {
			Objects.requireNonNull(type, "type is null");
			Objects.requireNonNull(instance, "instance is null");
			this.instances.put(type, instance);
			return this;
		}

		@NotNull
		public Builder debug() {
			return this.debug(true);
		}

		@NotNull
		public Builder debug(boolean debug) {
			this.debug = debug;
			return this;
		}

		@NotNull
		public ServiceContext build() {
			return new ServiceContext(this.instances, this.debug);
		}
	}
}
