package io.github.wsyong11.gameforge.framework.system.log.core.adapter;

import io.github.wsyong11.gameforge.framework.system.log.core.config.LogConfigManager;
import io.github.wsyong11.gameforge.framework.system.log.core.logger.LoggerFactory;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.nio.file.Path;

public interface LogSystemAdapter {
	@NotNull
	String getId();

	@NotNull
	LoggerFactory getLoggerFactory(@NotNull ClassLoader classLoader);

	@NotNull
	LogConfigManager getConfigManager(@NotNull ClassLoader classLoader);

	void bindClassLoader(@NotNull ClassLoader classLoader);

	void unbindClassLoader(@NotNull ClassLoader classLoader);

	void setDefaultStdout(PrintStream stdout);

	void setDefaultStderr(PrintStream stderr);

	void init();

	void destroy();

	void setLogDir(@NotNull Path logDir);
}
