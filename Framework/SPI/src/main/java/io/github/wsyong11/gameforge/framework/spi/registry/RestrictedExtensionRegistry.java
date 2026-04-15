package io.github.wsyong11.gameforge.framework.spi.registry;

import io.github.wsyong11.gameforge.framework.spi.ExtensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RestrictedExtensionRegistry extends ForwardingExtensionRegistry {
	private final Set<ExtensionType<?>> allowedTypes;

	public RestrictedExtensionRegistry(@NotNull ExtensionRegistry delegate, @NotNull Collection<ExtensionType<?>> types) {
		super(Objects.requireNonNull(delegate, "delegate is null"));
		Objects.requireNonNull(types, "types is null");

		this.allowedTypes = Set.copyOf(types);
	}

	@Override
	public <T> void register(@NotNull ExtensionType<T> type, @NotNull T instance) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		if (!this.allowedTypes.contains(type))
			throw new IllegalArgumentException("Extension type is not allowed " + type);

		super.register(type, instance);
	}

	@Override
	public <T> void unregister(@NotNull ExtensionType<T> type, @NotNull T instance) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		if (!this.allowedTypes.contains(type))
			return;

		super.unregister(type, instance);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void unregister(@NotNull T instance) {
		Objects.requireNonNull(instance, "instance is null");

		for (ExtensionType<?> type : this.allowedTypes)
			super.unregister((ExtensionType<? super T>) type, instance);
	}

	@Override
	public <T> void setPriority(@NotNull ExtensionType<T> type, @NotNull T instance, int priority) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		if (!this.allowedTypes.contains(type))
			throw new IllegalArgumentException("Extension type is not allowed " + type);

		super.setPriority(type, instance, priority);
	}

	@Override
	public <T> int getPriority(@NotNull ExtensionType<T> type, @NotNull T instance) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		if (!this.allowedTypes.contains(type))
			throw new IllegalArgumentException("Extension type is not allowed " + type);

		return super.getPriority(type, instance);
	}

	@NotNull
	@Unmodifiable
	@Override
	public <T> List<T> getExtensions(@NotNull ExtensionType<T> type) {
		Objects.requireNonNull(type, "type is null");

		if (!this.allowedTypes.contains(type))
			return List.of();

		return super.getExtensions(type);
	}

	@Nullable
	@Override
	public <T> T getExtension(@NotNull ExtensionType<T> type) {
		Objects.requireNonNull(type, "type is null");

		if (!this.allowedTypes.contains(type))
			return null;

		return super.getExtension(type);
	}

	@Override
	public void clear(@NotNull ExtensionType<?> type) {
		Objects.requireNonNull(type, "type is null");

		if (!this.allowedTypes.contains(type))
			return;

		super.clear(type);
	}
}
