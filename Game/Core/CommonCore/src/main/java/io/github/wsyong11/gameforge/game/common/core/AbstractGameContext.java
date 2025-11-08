package io.github.wsyong11.gameforge.game.common.core;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.event.EventBus;
import io.github.wsyong11.gameforge.framework.event.bus.DebugEventBus;
import io.github.wsyong11.gameforge.framework.lifecycle.ILifecycle;
import io.github.wsyong11.gameforge.game.common.Game;
import io.github.wsyong11.gameforge.game.common.GameContext;
import io.github.wsyong11.gameforge.game.common.GameSide;
import io.github.wsyong11.gameforge.game.common.service.IService;
import io.github.wsyong11.gameforge.game.common.service.ServiceRegistry;
import io.github.wsyong11.gameforge.util.exception.RuntimeInterruptedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractGameContext implements GameContext {
	private final GameSide side;
	private final Game game;

	private final ServiceRegistry serviceRegistry;

	public AbstractGameContext(@NotNull GameSide side, @NotNull Game game, @NotNull ServiceRegistry serviceRegistry) {
		Objects.requireNonNull(side, "side is null");
		Objects.requireNonNull(game, "game is null");
		Objects.requireNonNull(serviceRegistry, "serviceRegistry is null");

		this.side = side;
		this.game = game;

		this.serviceRegistry = serviceRegistry;
	}

	@NotNull
	@Override
	public GameSide getSide() {
		return this.side;
	}

	@NotNull
	@Override
	public ILifecycle getLifecycle() {
		return this.game.getLifecycle();
	}

	// -------------------------------------------------------------------------------------------------------------- //
	@Nullable
	@Override
	public <T extends IService> T getServiceUnsafe(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");
		return this.serviceRegistry.getServiceUnsafe(type);
	}

	@Nullable
	@Override
	public <T extends IService> T getServiceUnsafe(@NotNull Identifier id) {
		Objects.requireNonNull(id, "id is null");
		return this.serviceRegistry.getServiceUnsafe(id);
	}

	@Nullable
	@Override
	public <T extends IService> T requireService(@NotNull Class<T> type, long timeoutMs) throws RuntimeInterruptedException {
		Objects.requireNonNull(type, "type is null");
		return this.serviceRegistry.requireService(type, timeoutMs);
	}

	@Nullable
	@Override
	public <T extends IService> T requireService(@NotNull Identifier id, long timeoutMs) throws RuntimeInterruptedException {
		Objects.requireNonNull(id, "id is null");
		return this.serviceRegistry.requireService(id, timeoutMs);
	}
}
