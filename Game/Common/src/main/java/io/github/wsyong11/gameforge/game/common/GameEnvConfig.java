package io.github.wsyong11.gameforge.game.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.nio.file.Path;
import java.util.List;

public interface GameEnvConfig {
	@NotNull
	Path getLogDir();

	@NotNull
	Path getTempDir();

	@NotNull
	Path getModDir();

	@NotNull
	Path getConfigDir();

	@NotNull
	Path getCrashReportDir();

	boolean isDebug();

	@NotNull
	@UnmodifiableView
	List<Path> getModJarPaths();

	boolean isSafeMode();
}
