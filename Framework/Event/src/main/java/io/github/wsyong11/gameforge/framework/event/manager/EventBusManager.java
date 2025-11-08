package io.github.wsyong11.gameforge.framework.event.manager;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.event.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.concurrent.ExecutorService;

public interface EventBusManager extends IEventBusManager, AutoCloseable {
	void registerEventBus(@NotNull Identifier id, @NotNull EventBus eventBus);

	boolean unregister(@NotNull Identifier id);

	@Nullable
	@Override
	EventBus getEventBus(@NotNull Identifier id);

	@NotNull
	@Unmodifiable
	Set<Identifier> getRegistered();

	@Override
	void close();
}
