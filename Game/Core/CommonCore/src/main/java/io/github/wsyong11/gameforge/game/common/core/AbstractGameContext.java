package io.github.wsyong11.gameforge.game.common.core;

import io.github.wsyong11.gameforge.framework.event.EventBus;
import io.github.wsyong11.gameforge.framework.event.bus.DebugEventBus;
import io.github.wsyong11.gameforge.framework.lifecycle.ILifecycle;
import io.github.wsyong11.gameforge.framework.lifecycle.LifecycleInstance;
import io.github.wsyong11.gameforge.framework.system.resource.manage.DefaultResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import io.github.wsyong11.gameforge.game.common.Game;
import io.github.wsyong11.gameforge.game.common.GameContext;
import io.github.wsyong11.gameforge.game.common.GameSide;
import io.github.wsyong11.gameforge.game.common.registry.RegistryManager;
import io.github.wsyong11.gameforge.game.common.tick.TickManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractGameContext extends LifecycleInstance implements GameContext {
	private final GameSide side;
	private final Game game;

	private final ResourceManager resourceManager;

	private final EventBus globalEventBus;

	public AbstractGameContext(@NotNull GameSide side, @NotNull Game game, @NotNull ResourceManager resourceManager) {
		Objects.requireNonNull(side, "side is null");
		Objects.requireNonNull(game, "game is null");

		this.side = side;
		this.game = game;

		this.resourceManager = resourceManager;

		this.globalEventBus = DebugEventBus.wrap(EventBus.simple(), "game");
	}

	@NotNull
	@Override
	public GameSide getSide() {
		return this.side;
	}

	@NotNull
	@Override
	public RegistryManager getRegistry() {
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	public EventBus getEventBus() {
		return this.globalEventBus;
	}

	@NotNull
	@Override
	public TickManager getTickManager() {
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	public ResourceManager getResourceManager() {
		return this.resourceManager;
	}

	@NotNull
	@Override
	public ILifecycle getLifecycle() {
		return this.game.getLifecycle();
	}
}
