package io.github.wsyong11.gameforge.game.core.server;

import io.github.wsyong11.gameforge.game.common.Game;
import io.github.wsyong11.gameforge.game.common.GameSide;
import io.github.wsyong11.gameforge.game.common.core.AbstractGameContext;
import io.github.wsyong11.gameforge.game.common.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;

public class ServerGameContext extends AbstractGameContext {
	public ServerGameContext(@NotNull Game game, @NotNull ServiceRegistry serviceRegistry) {
		super(GameSide.SERVER, game, serviceRegistry);
	}
}
