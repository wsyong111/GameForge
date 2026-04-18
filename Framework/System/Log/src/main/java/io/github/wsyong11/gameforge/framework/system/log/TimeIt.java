package io.github.wsyong11.gameforge.framework.system.log;

import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class TimeIt implements AutoCloseable {
	private static final TimeIt NOOP_IMPL = new TimeIt() {
		@Override
		public void close() { /* no-op */ }
	};

	private TimeIt() { /* no-op */ }

	@NotNull
	public static TimeIt begin(@NotNull Logger logger, @NotNull String message) {
		Objects.requireNonNull(logger, "logger is null");
		Objects.requireNonNull(message, "message is null");
		return logger.isDebugEnabled() ? new Impl(logger, message, LogLevel.DEBUG) : NOOP_IMPL;
	}

	@NotNull
	public static TimeIt begin(@NotNull Logger logger, @NotNull LogLevel level, @NotNull String message) {
		Objects.requireNonNull(logger, "logger is null");
		Objects.requireNonNull(level, "level is null");
		Objects.requireNonNull(message, "message is null");
		return logger.isLevelEnabled(level) ? new Impl(logger, message, level) : NOOP_IMPL;
	}

	@Override
	public abstract void close();

	private static class Impl extends TimeIt {
		private final Logger logger;
		private final String message;
		private final LogLevel level;

		private final long startTimeNs;

		public Impl(@NotNull Logger logger, @NotNull String message, @NotNull LogLevel level) {
			Objects.requireNonNull(logger, "logger is null");
			Objects.requireNonNull(message, "message is null");
			Objects.requireNonNull(level, "level is null");

			this.startTimeNs = System.nanoTime();

			this.logger = logger;
			this.message = message;
			this.level = level;
		}

		@NotNull
		private static String formatElapsedTime(long elapsedNs) {
			if (elapsedNs < 1_000) {
				return elapsedNs + " ns";
			} else if (elapsedNs < 1_000_000) {
				return String.format("%d μs", elapsedNs / 1_000L);
			} else if (elapsedNs < 1_000_000_000) {
				return String.format("%.2f ms", elapsedNs / 1_000_000.0D);
			} else {
				return String.format("%.2f s", elapsedNs / 1_000_000_000.0D);
			}
		}

		@Override
		public void close() {
			long endTimeNs = System.nanoTime();
			long elapsedTimeNs = endTimeNs - this.startTimeNs;

			String message = this.message + ", elapsed " + formatElapsedTime(elapsedTimeNs);

			switch (this.level) {
				case TRACE -> this.logger.trace(message);
				case DEBUG -> this.logger.debug(message);
				case INFO -> this.logger.info(message);
				case WARN -> this.logger.warn(message);
				case ERROR -> this.logger.error(message);
			}
		}
	}
}
