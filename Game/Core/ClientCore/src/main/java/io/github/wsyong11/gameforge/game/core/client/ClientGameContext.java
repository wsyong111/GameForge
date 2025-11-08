package io.github.wsyong11.gameforge.game.core.client;

import io.github.wsyong11.gameforge.game.common.Game;
import io.github.wsyong11.gameforge.game.common.GameSide;
import io.github.wsyong11.gameforge.game.common.core.AbstractGameContext;
import io.github.wsyong11.gameforge.game.common.service.IService;
import io.github.wsyong11.gameforge.game.common.service.ServiceRegistry;
import io.github.wsyong11.gameforge.util.exception.RuntimeInterruptedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientGameContext extends AbstractGameContext {
	public ClientGameContext(@NotNull Game game, @NotNull ServiceRegistry serviceRegistry) {
		super(GameSide.CLIENT, game, serviceRegistry);
	}
}
