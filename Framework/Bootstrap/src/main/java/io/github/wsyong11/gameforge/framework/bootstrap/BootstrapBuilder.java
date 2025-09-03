package io.github.wsyong11.gameforge.framework.bootstrap;

import io.github.wsyong11.gameforge.framework.app.Application;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class BootstrapBuilder<T> {
	private Path logDir = Path.of("log");

	private boolean debug = false;

	private Supplier<Function<T, Application>> mainClassFactory = null;

	private LogLevel logLevel = LogLevel.INFO;

	@Nullable
	private T config = null;

	@NotNull
	public BootstrapBuilder<T> enableDebug(boolean enabled) {
		this.debug = enabled;
		return this;
	}

	@NotNull
	public BootstrapBuilder<T> enableDebug() {
		return this.enableDebug(true);
	}

	@NotNull
	public BootstrapBuilder<T> logDir(@NotNull Path logDir) {
		Objects.requireNonNull(logDir, "logDir is null");
		this.logDir = logDir;
		return this;
	}

	@NotNull
	public BootstrapBuilder<T> mainClass(@NotNull Supplier<Function<T,Application>> mainClassFactory) {
		Objects.requireNonNull(mainClassFactory, "mainClassFactory is null");
		this.mainClassFactory = mainClassFactory;
		return this;
	}

	@NotNull
	public BootstrapBuilder<T> logLevel(@NotNull LogLevel level) {
		Objects.requireNonNull(level, "level is null");
		this.logLevel = level;
		return this;
	}

	@NotNull
	public BootstrapBuilder<T> config(@Nullable T config) {
		this.config = config;
		return this;
	}

	@NotNull
	public Bootstrap<T> build() {
		if (this.mainClassFactory == null)
			throw new IllegalArgumentException("Main class factory is not set");

		return new Bootstrap<>(this.mainClassFactory, new BootstrapConfiguration<>(
			this.logDir,
			this.debug,
			this.logLevel,
			this.config
		));
	}
}
