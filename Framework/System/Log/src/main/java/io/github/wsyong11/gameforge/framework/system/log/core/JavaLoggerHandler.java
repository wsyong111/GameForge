package io.github.wsyong11.gameforge.framework.system.log.core;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

class JavaLoggerHandler extends Handler {
	public static void install() {
		LogManager logManager = LogManager.getLogManager();
		logManager.reset();
		logManager.getLogger("").addHandler(new JavaLoggerHandler());
	}

	@NotNull
	private static LogLevel castLevel(@NotNull Level level) {
		Objects.requireNonNull(level, "level is null");

		if (level == Level.SEVERE)
			return LogLevel.ERROR;

		if (level == Level.WARNING)
			return LogLevel.WARN;

		if (level == Level.INFO || level == Level.CONFIG)
			return LogLevel.INFO;

		if (level == Level.FINE || level == Level.FINER)
			return LogLevel.DEBUG;

		if (level == Level.FINEST || level.intValue() <= Level.FINEST.intValue())
			return LogLevel.TRACE;

		return LogLevel.INFO;
	}

	@Override
	public void publish(@Nullable LogRecord record) {
		if (record == null)
			return;

		String message = record.getMessage();
		if (message == null)
			return;

		Level level = record.getLevel();
		if (level == Level.OFF)
			return;

		Logger logger = this.getLogger(record);
		Throwable thrown = record.getThrown();
		Object[] parameters = record.getParameters();

		LogLevel logLevel = castLevel(level);
		this.log(logger, logLevel, message, parameters, thrown);
	}

	private void log(
		@NotNull Logger logger,
		@NotNull LogLevel logLevel,
		@NotNull String message,
		@Nullable Object[] parameters,
		@Nullable Throwable thrown
	) {
		Objects.requireNonNull(logger, "logger is null");
		Objects.requireNonNull(logLevel, "logLevel is null");
		Objects.requireNonNull(message, "message is null");

		Object[] fullParameters = ArrayUtils.EMPTY_OBJECT_ARRAY;
		if (parameters != null)
			fullParameters = parameters;

		if (thrown != null)
			fullParameters = ArrayUtils.add(fullParameters, thrown);

		if (logLevel == LogLevel.TRACE) logger.trace(message, fullParameters);
		if (logLevel == LogLevel.DEBUG) logger.debug(message, fullParameters);
		if (logLevel == LogLevel.INFO) logger.info(message, fullParameters);
		if (logLevel == LogLevel.WARN) logger.warn(message, fullParameters);
		if (logLevel == LogLevel.ERROR) logger.error(message, fullParameters);
	}

	@NotNull
	protected Logger getLogger(@NotNull LogRecord record) {
		Objects.requireNonNull(record, "record is null");

		String loggerName = record.getLoggerName();
		if (loggerName != null)
			return Log.getLogger(loggerName);

		loggerName = record.getSourceClassName();
		if (loggerName != null)
			return Log.getLogger(loggerName);

		return Log.getLogger();
	}

	@Override
	public Level getLevel() {
		return Level.ALL;
	}

	@Override
	public void flush() { /* no-op */ }

	@Override
	public void close() { /* no-op */ }
}
