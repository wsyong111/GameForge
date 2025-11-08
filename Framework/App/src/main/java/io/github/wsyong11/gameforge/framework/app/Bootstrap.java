package io.github.wsyong11.gameforge.framework.app;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;
import io.github.wsyong11.gameforge.framework.system.log.core.config.LoggerConfig;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public class Bootstrap {
	private static final Logger LOGGER = Log.getLogger();

	private final ApplicationFactory factory;

	private final Map<BootstrapConfig<?>, Object> configs;
	private LogLevel logLevel;
	private Path logDir;
	private boolean debug;

	private volatile boolean started;

	public Bootstrap(@NotNull ApplicationFactory factory) {
		Objects.requireNonNull(factory, "factory is null");

		this.factory = factory;

		this.configs = new HashMap<>();
		this.logLevel = LogLevel.INFO;
		this.logDir = Path.of("log");
		this.debug = false;

		this.started = false;
	}

	protected void assertStarted() {
		if (this.started) throw new IllegalStateException("Application is started");
	}

	@NotNull
	public Bootstrap logLevel(@NotNull LogLevel logLevel) {
		Objects.requireNonNull(logLevel, "logLevel is null");
		this.assertStarted();
		this.logLevel = logLevel;
		return this;
	}

	@NotNull
	public Bootstrap logDir(@NotNull Path logDir) {
		Objects.requireNonNull(logDir, "logDir is null");
		this.assertStarted();
		this.logDir = logDir;
		return this;
	}

	@NotNull
	public Bootstrap debug(boolean debug) {
		this.assertStarted();
		this.debug = debug;
		return this;
	}

	@NotNull
	public <T> Bootstrap config(@NotNull BootstrapConfig<T> config, T value) {
		Objects.requireNonNull(config, "config is null");
		this.assertStarted();
		this.configs.put(config, value);
		return this;
	}

	public int start() throws Throwable {
		this.assertStarted();
		this.started = true;

		LogManager.setLogDir(this.logDir);
		LogManager logManager = LogManager.getInstance(Bootstrap.class.getClassLoader());
		LoggerConfig rootLoggerConfig = logManager.getRootLoggerConfig();
		rootLoggerConfig.setLevel(this.logLevel);
		rootLoggerConfig.update();

		BootstrapContext context = new BootstrapContext(
			this.logDir,
			this.logLevel,
			this.debug,
			Map.copyOf(this.configs)
		);
		LOGGER.info("Initializing application with context {}", lazy(context));

		try {
			Application application = this.factory.create(context);
			LOGGER.info("Start application");
			int exitCode = application.main();

			LOGGER.info("Application exited, exit code: {}", "0x%08X".formatted(exitCode));
			return exitCode;
		} catch (Throwable e) {
			LOGGER.error("Uncaught exceptions were found", e);
			throw e;
		}
	}
}
