package io.github.wsyong11.gameforge.game.core.client;

import io.github.wsyong11.gameforge.framework.system.input.DefaultInputManager;
import io.github.wsyong11.gameforge.framework.system.input.InputManager;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.render.ex.RenderSystemInitiationException;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.window.Window;
import io.github.wsyong11.gameforge.framework.system.window.listener.WindowListener;
import io.github.wsyong11.gameforge.game.common.core.AbstractGame;
import io.github.wsyong11.gameforge.game.common.core.StartupConfig;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.LockSupport;

public class ClientGame extends AbstractGame {
	private static final Logger LOGGER = Log.getLogger();

	private RenderThread renderThread;
	private InputManager inputManager;

	public ClientGame(@NotNull StartupConfig config) {
		super(config, ResourcePath.of("assets"));

		this.renderThread = null;
		this.inputManager = null;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private void initRenderThread() throws RenderSystemInitiationException, InterruptedException {
		this.renderThread = new RenderThread(
			this.getResourceManager(),
			this.getConfig().isDebug()
		);
		this.renderThread.init();
	}

	private void initInputManager() {
		this.inputManager = new DefaultInputManager();

		Window window = this.renderThread.getWindow();
		window.addWindowListener(new WindowListener() {
			@Override
			public void onClose() {
				requireStop();
			}
		});
	}

	@Override
	protected void onStarting() throws Throwable {
		super.onStarting();

		this.initRenderThread();
		this.initInputManager();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	protected void onRunning() throws Throwable {
		super.onRunning();

		this.renderThread.runThread();
		this.mainLoop();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	protected void onStopping() throws Throwable {
		super.onStopping();

		this.renderThread.close();
		LOGGER.info("Waiting render thread exit");
		this.renderThread.join();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
