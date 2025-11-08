package io.github.wsyong11.gameforge.framework.event.manager;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.event.IEventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IEventBusManager {
	@Nullable
	IEventBus getEventBus(@NotNull Identifier id);

	@NotNull
	default IEventBus requireEventBus(@NotNull Identifier id) {
		IEventBus eventBus = this.getEventBus(id);
		if (eventBus == null)
			throw new IllegalStateException("Event bus id " + id + " not register");
		return eventBus;
	}

	default boolean isRegistered(@NotNull Identifier id) {
		return this.getEventBus(id) != null;
	}
}
