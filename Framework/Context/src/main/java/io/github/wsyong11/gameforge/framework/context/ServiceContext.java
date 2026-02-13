package io.github.wsyong11.gameforge.framework.context;

import io.github.wsyong11.gameforge.framework.context.annotation.UsingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ServiceContext extends Context {
	@UsingContext(require = true)
	@NotNull
	public static <T> Optional<T> getInstanceOptional(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		return getEachStream()
			.map(c -> c.asUnsafe(ServiceContext.class))
			.filter(Objects::nonNull)
			.map(c -> c.getUnsafe(type))
			.filter(Objects::nonNull)
			.findFirst();
	}

	private final Map<Class<?>, Object> instances;

	public ServiceContext(@NotNull Map<Class<?>, Object> instances, boolean debug) {
		super(debug);
		Objects.requireNonNull(instances, "instances is null");

		this.instances = Map.copyOf(instances);
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
}
