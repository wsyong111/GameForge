package io.github.wsyong11.gameforge.framework.context;

import io.github.wsyong11.gameforge.framework.context.annotation.UsingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;

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
		private static final Object NULL = new Object();

		private static final ThreadLocal<Integer> lastContextStackId = ThreadLocal.withInitial(() -> 0);
		private static final ThreadLocal<Map<Pair<Object, Class<?>>, WeakReference<Object>>> instanceCache = ThreadLocal.withInitial(HashMap::new);

		@SuppressWarnings("unchecked")
		@UsingContext
		@Nullable
		private static <T extends Instance, R> R getCache(@NotNull Object key, @NotNull Class<T> type, @NotNull Function<Class<T>, R> getter) {
			Objects.requireNonNull(key, "key is null");
			Objects.requireNonNull(type, "type is null");
			Objects.requireNonNull(getter, "getter is null");

			Map<Pair<Object, Class<?>>, WeakReference<Object>> cache = instanceCache.get();

			int currentStackSize = Context.getStackCacheId();
			if (lastContextStackId.get() != currentStackSize) {
				cache.clear();
				lastContextStackId.set(currentStackSize);
			}

			Pair<Object, Class<?>> keyPair = Pair.of(key, type);

			WeakReference<Object> cachedValueRef = cache.get(keyPair);
			Object cachedValue;
			if (cachedValueRef == null || (cachedValue = cachedValueRef.get()) == null) {
				R value = getter.apply(type);
				cachedValue = value == null ? NULL : value;
				cache.put(keyPair, new WeakReference<>(cachedValue));
			}

			return (R) (cachedValue == NULL ? null : cachedValue);
		}

		private static final Object SINGLE_INSTANCE_KEY = new Object();

		@Nullable
		@UsingContext
		protected static <T extends Instance> T getInstanceUnsafe(@NotNull Class<T> type) {
			Objects.requireNonNull(type, "type is null");
			return getCache(SINGLE_INSTANCE_KEY, type, (t) -> Context
				.getEachStream()
				.map(c -> c.asUnsafe(ServiceContext.class))
				.filter(Objects::nonNull)
				.map(c -> c.getUnsafe(t))
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null));
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

		private static final Object MULTI_INSTANCE_KEY = new Object();

		@NotNull
		@UsingContext
		@Unmodifiable
		protected static <T extends Instance> List<T> getInstances(@NotNull Class<T> type) {
			Objects.requireNonNull(type, "type is null");

			List<T> result = getCache(MULTI_INSTANCE_KEY, type, (t) -> Context
				.getEachStream()
				.map(c -> c.asUnsafe(ServiceContext.class))
				.filter(Objects::nonNull)
				.map(c -> c.getUnsafe(t))
				.filter(Objects::nonNull)
				.toList());
			assert result != null;
			return result;
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
