package io.github.wsyong11.gameforge.game.core.server;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.game.common.core.AbstractGame;
import io.github.wsyong11.gameforge.game.common.core.StartupConfig;
import io.github.wsyong11.gameforge.util.io.CallbackPrintStream;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.nio.charset.Charset;

public class ServerGame extends AbstractGame {
	private static final Logger LOGGER = Log.getLogger();

	private CommandPrompt commandPrompt;

	public ServerGame(@NotNull StartupConfig config) {
		super(config, ResourcePath.of("data"));

		this.commandPrompt = null;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	protected void onStarting() throws Throwable {
		super.onStarting();
		this.commandPrompt = new CommandPrompt();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	protected void tick() {

	}

	@Override
	protected void onRunning() throws Throwable {
		super.onRunning();

		this.commandPrompt.start();
		this.commandPrompt.addInputListener(l -> {
			if (l.equalsIgnoreCase("exit"))
				requireStop();
		});

		this.mainLoop();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	protected void onStopping() throws Throwable {
		LOGGER.debug("Closing command prompt");
		try {
			this.commandPrompt.close();
		} catch (IOException e) {
			LOGGER.warn("Failed to close command prompt", e);
		}

		super.onStopping();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
