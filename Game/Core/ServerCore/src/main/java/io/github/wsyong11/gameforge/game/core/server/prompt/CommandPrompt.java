package io.github.wsyong11.gameforge.game.core.server.prompt;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;
import io.github.wsyong11.gameforge.util.io.CallbackPrintStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;

import java.io.Closeable;
import java.io.IOError;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Objects;

public class CommandPrompt extends Thread implements Closeable {
	private static final Logger LOGGER = Log.getLogger();

	private static final String PROMPT = "$> ";

	private final Terminal terminal;
	private final LineReader lineReader;

	private final ListenerList listenerList;

	@Nullable
	private volatile PromptHighlighter highlighter;

	private volatile PrintStream defaultOut;
	private volatile PrintStream defaultErr;

	private volatile boolean closed;

	public CommandPrompt() throws IOException {
		this.terminal = TerminalBuilder
			.builder()
			.system(true)
			.build();

		this.lineReader = LineReaderBuilder
			.builder()
			.terminal(this.terminal)
			.highlighter(new JLineHighlighter())
			.build();

		this.listenerList = ListenerList.sync();

		this.highlighter = null;

		this.defaultOut = null;
		this.defaultErr = null;

		this.closed = false;

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

	public void setHighlighter(@Nullable PromptHighlighter highlighter) {
		this.highlighter = highlighter;
	}

	@Nullable
	public PromptHighlighter getHighlighter() {
		return this.highlighter;
	}

	@Override
	public synchronized void start() {
		this.defaultOut = LogManager.getDefaultStdout();
		this.defaultErr = LogManager.getDefaultStderr();

		Charset encoding = Charset.defaultCharset();

		CallbackPrintStream consolePrintStream = new CallbackPrintStream(this.lineReader::printAbove, encoding);
		LogManager.setDefaultStdout(consolePrintStream);
		LogManager.setDefaultStderr(consolePrintStream);

		super.start();
	}

	@Override
	public void run() {
		while (!this.closed) {
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

			this.listenerList.fire(
				InputListener.class,
				l -> l.onInput(input),
				ListenerExceptionCallback.log(LOGGER));
		}

		LogManager.setDefaultStdout(this.defaultOut);
		LogManager.setDefaultStderr(this.defaultErr);

		LOGGER.debug("Closed");
	}

	@Nullable
	private String readInput() {
		if (this.closed)
			return null;

		LOGGER.trace("Reading user input");
		try {
			return this.lineReader.readLine(PROMPT);
		} catch (UserInterruptException e) {
			if (this.closed)
				return null;

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

	public void addInputListener(@NotNull InputListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.add(InputListener.class, listener);
	}

	public void removeInputListener(@NotNull InputListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.remove(InputListener.class, listener);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public void close() throws IOException {
		if (this.closed)
			return;
		this.closed = true;

		LOGGER.debug("Closing terminal");

		this.interrupt();
		try {
			this.join();
		} catch (InterruptedException e) {
			LOGGER.debug("Interrupted when waiting command prompt close", e);
			Thread.currentThread().interrupt();
		}

		this.listenerList.clear();
		this.terminal.close();
	}

	public interface UserInterruptListener extends IListener {
		void onInterrupt();
	}

	public interface InputListener extends IListener {
		void onInput(@NotNull String text);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private class JLineHighlighter implements Highlighter {
		private final AttributedStringBuilder builder;

		private JLineHighlighter() {
			this.builder = new AttributedStringBuilder();
		}

		@NotNull
		@Override
		public AttributedString highlight(@NotNull LineReader reader, @NotNull String buffer) {
			Objects.requireNonNull(reader, "reader is null");
			Objects.requireNonNull(buffer, "buffer is null");

			if (buffer.isEmpty())
				return AttributedString.EMPTY;

			try {
				PromptHighlighter highlighter = CommandPrompt.this.highlighter;
				if (highlighter != null) {
					highlighter.highlight(reader, buffer, this.builder);
				} else {
					this.builder.append(buffer);
				}

				return this.builder.toAttributedString();
			} finally {
				this.builder.setLength(0);
			}
		}
	}
}
