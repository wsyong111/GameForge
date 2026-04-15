package io.github.wsyong11.gameforge.framework.spi.registry;

import io.github.wsyong11.gameforge.framework.spi.ExtensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExtensionRegistry {
	@NotNull
	static ExtensionRegistry create() {
		return new SimpleExtensionRegistry();
	}

	@NotNull
	static ExtensionRegistry createRestrict(@NotNull Set<ExtensionType<?>> allowTypes) {
		return restrict(create(), allowTypes);
	}

	@NotNull
	static ExtensionRegistry restrict(@NotNull ExtensionRegistry registry, @NotNull Set<ExtensionType<?>> allowTypes) {
		return new RestrictedExtensionRegistry(registry, allowTypes);
	}

	<T> void register(@NotNull ExtensionType<T> type, @NotNull T instance);

	<T> void unregister(@NotNull ExtensionType<T> type, @NotNull T instance);

	<T> void unregister(@NotNull T instance);

	<T> boolean has(@NotNull T extension);

	<T> boolean has(@NotNull ExtensionType<T> type);

	<T> void setPriority(@NotNull ExtensionType<T> type, @NotNull T instance, int priority);

	<T> int getPriority(@NotNull ExtensionType<T> type, @NotNull T instance);

	@NotNull
	@Unmodifiable
	<T> List<T> getExtensions(@NotNull ExtensionType<T> type);

	@Nullable
	<T> T getExtension(@NotNull ExtensionType<T> type);

	@NotNull
	default <T> Optional<T> getExtensionOptional(@NotNull ExtensionType<T> type) {
		return Optional.ofNullable(this.getExtension(type));
	}

	void clear();

	void clear(@NotNull ExtensionType<?> type);
}
