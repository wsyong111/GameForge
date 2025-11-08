package io.github.wsyong11.gameforge.game.common.service;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.event.manager.EventBusManager;
import io.github.wsyong11.gameforge.framework.event.manager.IEventBusManager;
import org.jetbrains.annotations.NotNull;

public interface EventBusService extends StubService<EventBusManager>, IEventBusManager {
	Identifier SYSTEM = Identifier.withDefaultNamespace("system");

	@NotNull
	@Override
	default String getServiceName() {
		return EventBusService.class.getName();
	}
}
