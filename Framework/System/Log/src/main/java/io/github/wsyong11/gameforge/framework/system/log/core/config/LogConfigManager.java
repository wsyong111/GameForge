package io.github.wsyong11.gameforge.framework.system.log.core.config;

import org.jetbrains.annotations.NotNull;

public interface LogConfigManager {
	@NotNull
	LoggerConfig getRootConfig();

	@NotNull
	LoggerConfig getConfig(@NotNull String name);
}
