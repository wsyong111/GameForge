package io.github.wsyong11.gameforge.game.common.service;

import io.github.wsyong11.gameforge.framework.tick.TickManager;
import org.jetbrains.annotations.NotNull;

public interface TickService extends StubService<TickManager>, TickManager {
	@NotNull
	@Override
	default String getServiceName() {
		return TickService.class.getName();
	}
}
