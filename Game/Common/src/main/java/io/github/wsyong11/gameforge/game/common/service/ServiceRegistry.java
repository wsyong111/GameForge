package io.github.wsyong11.gameforge.game.common.service;

import io.github.wsyong11.gameforge.framework.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface ServiceRegistry extends IService, ServiceProvider {
	@NotNull
	@Override
	default String getServiceName() {
		return ServiceRegistry.class.getName();
	}

	<T extends IService> void register(@NotNull Class<? super T> type, @NotNull T instance);

	default <T extends IService> void register(@NotNull String id, @NotNull T instance) {
		Objects.requireNonNull(id, "id is null");
		this.register(Identifier.withDefaultNamespace(id), instance);
	}

	<T extends IService> void register(@NotNull Identifier id, @NotNull T instance);

	boolean unregister(@NotNull Class<? super IService> type);

	default boolean unregister(@NotNull String id) {
		Objects.requireNonNull(id, "id is null");
		return this.unregister(Identifier.withDefaultNamespace(id));
	}

	boolean unregister(@NotNull Identifier id);
}
