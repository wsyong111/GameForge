package io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2.config;

import io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2.LogLevelConvertor;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.log.core.config.LoggerConfig;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Log4jLoggerConfig implements LoggerConfig {
	private final org.apache.logging.log4j.core.config.LoggerConfig config;
	private final UpdateCallback updateCallback;

	public Log4jLoggerConfig(@NotNull org.apache.logging.log4j.core.config.LoggerConfig config, @NotNull UpdateCallback updateCallback) {
		Objects.requireNonNull(config, "config is null");
		Objects.requireNonNull(updateCallback, "updateCallback is null");
		this.config = config;
		this.updateCallback = updateCallback;
	}

	@NotNull
	@Override
	public String getName() {
		return this.config.getName();
	}

	@Nullable
	@Override
	public LogLevel getLevel() {
		return LogLevelConvertor.toLevel(this.config.getExplicitLevel());
	}

	@Override
	public void setLevel(@Nullable LogLevel level) {
		Level log4jLevel = LogLevelConvertor.toLog4jLevel(level);
		if (this.config.getExplicitLevel() == log4jLevel) return;

		this.config.setLevel(log4jLevel);
		this.update();
	}

	@Override
	public void update() {
		this.updateCallback.update();
	}
}
