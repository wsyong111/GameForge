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
		this.commandPrompt.addUserInterruptListener(() -> {
			LOGGER.info("Stopping command prompt thread");
			this.commandPrompt.interrupt();
		});

		this.redirectTerminal();
	}

	private void redirectTerminal() {
		Terminal terminal = this.commandPrompt.getTerminal();
		LineReader lineReader = this.commandPrompt.getLineReader();

		Charset encoding = terminal.outputEncoding();

		CallbackPrintStream consolePrintStream = new CallbackPrintStream(lineReader::printAbove, encoding);
		LogManager.setDefaultStdout(consolePrintStream);
		LogManager.setDefaultStderr(consolePrintStream);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	protected void onRunning() throws Throwable {
		super.onRunning();

		this.commandPrompt.start();

	}

	@Override
	protected void onDestroy() {
		LOGGER.debug("Closing command prompt");
		try {
			this.commandPrompt.close();
		} catch (IOException e) {
			LOGGER.warn("Failed to close command prompt", e);
		}

		super.onDestroy();
	}
}
