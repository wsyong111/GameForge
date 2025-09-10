package io.github.wsyong11.gameforge.game.core.client;

import io.github.wsyong11.gameforge.framework.system.render.RenderSystem;
import io.github.wsyong11.gameforge.framework.system.render.impl.opengl.OpenGL330RenderEngine;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.game.common.core.AbstractGame;
import io.github.wsyong11.gameforge.game.common.core.StartupConfig;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class ClientGame extends AbstractGame {
	private RenderSystem renderSystem;

	public ClientGame(@NotNull StartupConfig config) {
		super(config, ResourcePath.of("assets"));

		this.renderSystem = null;
	}

	@Override
	protected void onStarting() throws Throwable {
		super.onStarting();

		this.renderSystem = RenderSystem.init(
			this.getResourceManager(),
			new Vector2i(800, 600),
			true,
			() -> OpenGL330RenderEngine::new
		);
	}

	@Override
	protected void onRunning() throws Throwable {
		super.onRunning();


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}
