package io.github.wsyong11.gameforge.game.common;

import io.github.wsyong11.gameforge.framework.event.IEventBus;
import io.github.wsyong11.gameforge.framework.lifecycle.LifecycleProvider;
import io.github.wsyong11.gameforge.game.common.service.ServiceProvider;
import org.jetbrains.annotations.NotNull;

public interface GameContext extends LifecycleProvider, ServiceProvider {
	@NotNull
	GameSide getSide();
}
