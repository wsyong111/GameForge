package io.github.wsyong11.gameforge.game.common.service;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.util.exception.RuntimeInterruptedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface ServiceProvider {
	@NotNull
	default <T extends IService> T getService(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");
		T service = this.getServiceUnsafe(type);
		if (service == null)
			throw new IllegalStateException("Service " + type + " is not loaded");
		return service;
	}

	@NotNull
	default <T extends IService> T getService(@NotNull String name) {
		Objects.requireNonNull(name, "name is null");
		return this.getService(Identifier.withDefaultNamespace(name));
	}

	@NotNull
	default <T extends IService> T getService(@NotNull Identifier id) {
		Objects.requireNonNull(id, "id is null");
		T service = this.getServiceUnsafe(id);
		if (service == null)
			throw new IllegalStateException("Service " + id + " is not loaded");
		return service;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Nullable
	<T extends IService> T getServiceUnsafe(@NotNull Class<T> type);

	@Nullable
	default <T extends IService> T getServiceUnsafe(@NotNull String name) {
		Objects.requireNonNull(name, "name is null");
		return this.getServiceUnsafe(Identifier.withDefaultNamespace(name));
	}

	@Nullable
	<T extends IService> T getServiceUnsafe(@NotNull Identifier id);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	default <T extends IService> T requireService(@NotNull Class<T> type) throws RuntimeInterruptedException {
		Objects.requireNonNull(type, "type is null");
		return Objects.requireNonNull(this.requireService(type, -1L),"Require service return null");
	}

	@NotNull
	default <T extends IService> T requireService(@NotNull String name) throws RuntimeInterruptedException {
		Objects.requireNonNull(name, "name is null");
		return this.requireService(Identifier.withDefaultNamespace(name));
	}

	@NotNull
	default <T extends IService> T requireService(@NotNull Identifier id) throws RuntimeInterruptedException {
		Objects.requireNonNull(id, "id is null");
		return Objects.requireNonNull(this.requireService(id, -1L), "Require service return null");
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Nullable
	<T extends IService> T requireService(@NotNull Class<T> type, long timeoutMs) throws RuntimeInterruptedException;

	@Nullable
	default <T extends IService> T requireService(@NotNull String name, long timeoutMs) throws RuntimeInterruptedException {
		Objects.requireNonNull(name, "name is null");
		return this.requireService(Identifier.withDefaultNamespace(name));
	}

	@Nullable
	<T extends IService> T requireService(@NotNull Identifier id, long timeoutMs) throws RuntimeInterruptedException;
}
