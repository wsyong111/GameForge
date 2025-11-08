package io.github.wsyong11.gameforge.game.common.core.service;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.event.IEventBus;
import io.github.wsyong11.gameforge.framework.event.manager.EventBusManager;
import io.github.wsyong11.gameforge.game.common.service.EventBusService;
import io.github.wsyong11.gameforge.util.Wrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class EventBusServiceStub extends Wrapper<EventBusManager> implements EventBusService {
	public EventBusServiceStub(@NotNull EventBusManager manager) {
		super(Objects.requireNonNull(manager, "manager is null"));
	}

	@Nullable
	@Override
	public IEventBus getEventBus(@NotNull Identifier id) {
		return this.delegate().getEventBus(id);
	}

	@NotNull
	@Override
	public IEventBus requireEventBus(@NotNull Identifier id) {
		return this.delegate().requireEventBus(id);
	}

	@Override
	public boolean isRegistered(@NotNull Identifier id) {
		return this.delegate().isRegistered(id);
	}

	@Nullable
	@Override
	public EventBusManager getDelegate() {
		return super.getDelegate();
	}
}
