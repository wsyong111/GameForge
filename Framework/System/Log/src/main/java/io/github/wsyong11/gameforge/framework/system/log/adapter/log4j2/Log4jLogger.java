package io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2;

import io.github.wsyong11.gameforge.framework.annotation.Internal;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.log.core.logger.AbstractLogger;
import io.github.wsyong11.gameforge.framework.system.log.templete.Template;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Internal
public class Log4jLogger extends AbstractLogger {
	private final Logger logger;

	public Log4jLogger(@NotNull Logger logger) {
		Objects.requireNonNull(logger, "logger is null");
		this.logger = logger;
	}

	@Override
	protected void log(@NotNull LogLevel level, @NotNull String message, @Nullable Object... args) {
		Template.process(args);
		this.logger.log(LogLevelConvertor.toLog4jLevel(level), message, args);
	}

	@NotNull
	@Override
	public String getName() {
		return this.logger.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return this.logger.isTraceEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return this.logger.isDebugEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return this.logger.isInfoEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return this.logger.isWarnEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return this.logger.isErrorEnabled();
	}
}
