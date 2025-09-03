package io.github.wsyong11.gameforge.framework.system.log.core.config;

import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LoggerConfig {
	@NotNull
	String getName();

	@Nullable
	LogLevel getLevel();

	void setLevel(@Nullable LogLevel level);

	default void update() {
	}
}
