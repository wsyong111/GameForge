package io.github.wsyong11.gameforge.game.core.server;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.Closeable;
import java.io.IOError;
import java.io.IOException;
import java.util.Objects;

public class CommandPrompt extends Thread implements Closeable {
	private static final Logger LOGGER = Log.getLogger();

	private static final String PROMPT = "$> ";

	private final Terminal terminal;
	private final LineReader lineReader;

	private final ListenerList listenerList;

	public CommandPrompt() throws IOException {
		this.terminal = TerminalBuilder
			.builder()
			.system(true)
			.build();

		this.lineReader = LineReaderBuilder
			.builder()
			.terminal(this.terminal)
			.build();

		this.listenerList = ListenerList.sync();

		this.setName("CommandPromptThread");
		this.setDaemon(true);
	}

	@NotNull
	public Terminal getTerminal() {
		return this.terminal;
	}

	@NotNull
	public LineReader getLineReader() {
		return this.lineReader;
	}

	@Override
	public void run() {
		while (true) {
			if (this.isInterrupted()) {
				this.interrupt();
				break;
			}

			String input;
			try {
				input = this.readInput();
			} catch (IOError e) {
				LOGGER.error("Critical IO error occurred while reading input", e);
				continue;
			} catch (EndOfFileException e) {
				LOGGER.error("Input stream closed", e);
				break;
			}

			if (input == null)
				continue;

			LOGGER.trace("User input: \"{}\"", input);

			if ("exit".equalsIgnoreCase(input))
				this.interrupt();
		}
	}

	@Nullable
	private String readInput() {
		LOGGER.trace("Reading user input");
		try {
			return this.lineReader.readLine(PROMPT);
		} catch (UserInterruptException e) {
			LOGGER.trace("Handled Ctrl + C");
			this.listenerList.fire(UserInterruptListener.class,
				UserInterruptListener::onInterrupt,
				ListenerExceptionCallback.log(LOGGER));
			return null;
		}
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public void addUserInterruptListener(@NotNull UserInterruptListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.add(UserInterruptListener.class, listener);
	}

	public void removeUserInterruptListener(@NotNull UserInterruptListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.remove(UserInterruptListener.class, listener);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public void close() throws IOException {
		this.interrupt();
		this.listenerList.clear();
		this.terminal.close();
	}

	public interface UserInterruptListener extends IListener {
		void onInterrupt();
	}
}
