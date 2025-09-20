package io.github.wsyong11.gameforge.game.core.server;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.game.common.core.AbstractGame;
import io.github.wsyong11.gameforge.game.common.core.StartupConfig;
import io.github.wsyong11.gameforge.game.core.server.prompt.CommandPrompt;
import io.github.wsyong11.gameforge.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import java.io.IOException;

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

		// test -n "hi" --name p1 p2 p3
//		this.commandPrompt.setHighlighter((reader, buffer, builder) -> {
//			String command;
//			if (buffer.startsWith("/")) {
//				builder.append(new AttributedString("/", AttributedStyle.DEFAULT
//					.foreground(77, 77, 77)));
//				command = buffer.substring(1);
//			} else {
//				command = buffer;
//			}
//
//			if (command.isEmpty())
//				return;
//
//			String[] tokens = command.split(" ");
//			String startToken = tokens[0];
//			if (!StringUtils.isLetterOrDigit(startToken)) {
//				builder.append(new AttributedString(command, AttributedStyle.DEFAULT
//					.foreground(AttributedStyle.RED)
//					.underline()));
//				return;
//			}
//
//			builder.append(new AttributedString(startToken, AttributedStyle.DEFAULT
//				.foreground(AttributedStyle.YELLOW)
//				.bold()));
//
//			if (tokens.length == 1)
//				return;
//
//			for (int i = 1; i < tokens.length; i++)
//				builder.append(' ')
//				       .append(tokens[i]);
//		});

		this.commandPrompt.addInputListener(l -> {
			if (l.equalsIgnoreCase("q"))
				requireStop();
		});
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	protected void onRunning() throws Throwable {
		super.onRunning();

		this.commandPrompt.start();

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
