package io.github.wsyong11.gameforge.framework.bootstrap;

import io.github.wsyong11.gameforge.framework.app.Application;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;
import io.github.wsyong11.gameforge.framework.system.log.core.config.LoggerConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class Bootstrap<T> implements AutoCloseable {
	private static final Logger LOGGER = Log.getLogger();

	@NotNull
	public static <V> BootstrapBuilder<V> builder() {
		return new BootstrapBuilder<>();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private final Supplier<Function<T, Application>> factory;
	private final BootstrapConfiguration<T> config;

	private Application app;

	public Bootstrap(@NotNull Supplier<Function<T, Application>> factory, @NotNull BootstrapConfiguration<T> config) {
		Objects.requireNonNull(factory, "factory is null");
		Objects.requireNonNull(config, "config is null");

		this.factory = factory;
		this.config = config;

		this.app = null;

		LogManager logManager = LogManager.getInstance(Bootstrap.class.getClassLoader());
		LoggerConfig rootLoggerConfig = logManager.getRootLoggerConfig();
		rootLoggerConfig.setLevel(this.config.getLogLevel());
	}

	@NotNull
	public BootstrapConfiguration<T> getBootstrapConfig() {
		return this.config;
	}

	@Nullable
	public T getConfig() {
		return this.config.getConfig();
	}

	public void start() {
		T config = this.getConfig();
		LOGGER.info("Initializing application with config {}", config);
		try {
			this.app = this.factory.get().apply(config);
		} catch (Exception e) {
			LOGGER.error("Cannot instantiation application main class", e);
			return;
		}

		LOGGER.info("Start application");
		try {
			this.app.start();
		} catch (Exception e) {
			LOGGER.error("Uncaught exceptions were found", e);
		}
	}

	@Override
	public void close() {
		try {
			this.app.stop();
		} catch (Exception e) {
			LOGGER.error("Exception in cleaning main class", e);
		}

		LOGGER.info("Cleaning data");

		this.app = null;
	}
}
