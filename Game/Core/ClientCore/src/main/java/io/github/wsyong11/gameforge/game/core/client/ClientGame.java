package io.github.wsyong11.gameforge.game.core.client;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.game.common.core.AbstractGame;
import io.github.wsyong11.gameforge.game.common.core.StartupConfig;
import org.jetbrains.annotations.NotNull;

public class ClientGame extends AbstractGame {
	public ClientGame(@NotNull StartupConfig config) {
		super(config, ResourcePath.of("assets"));
	}

	@Override
	protected void onStarting() throws Throwable {
		super.onStarting();
	}
}
