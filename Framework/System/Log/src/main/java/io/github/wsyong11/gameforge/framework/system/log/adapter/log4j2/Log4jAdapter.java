package io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2;

import io.github.wsyong11.gameforge.framework.annotation.Internal;
import io.github.wsyong11.gameforge.framework.env.EnvConfig;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.core.adapter.LogSystemAdapter;
import io.github.wsyong11.gameforge.framework.system.log.core.config.LogConfigManager;
import io.github.wsyong11.gameforge.framework.system.log.core.logger.LoggerFactory;
import io.github.wsyong11.gameforge.framework.system.log.core.logger.NoopLogger;
import io.github.wsyong11.gameforge.util.Lazy;
import com.google.auto.service.AutoService;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.NullConfiguration;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;
import org.apache.logging.log4j.status.StatusLogger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Internal
@AutoService(LogSystemAdapter.class)
public class Log4jAdapter implements LogSystemAdapter {
	private static final String CONFIG_NAME = "log4j2-isolation.xml";

	private static final Logger LOGGER = EnvConfig.DEBUG.getValue()
		? new Log4jLogger(StatusLogger.getLogger())
		: NoopLogger.INSTANCE;

	private static final Lazy<URL> CONFIG_FILE = Lazy.concurrentOf(() -> {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		URL resource = classLoader.getResource(CONFIG_NAME);
		if (resource == null)
			LOGGER.warn("Configuration file not found {}", CONFIG_NAME);
		return resource;
	});

	private final Map<ClassLoader, Context> contextMap;

	public Log4jAdapter() {
		this.contextMap = new ConcurrentHashMap<>();
	}

	@NotNull
	@Override
	public String getId() {
		return "log4j2";
	}

	@NotNull
	private Context getContext(@NotNull ClassLoader classLoader) {
		Objects.requireNonNull(classLoader, "classLoader is null");

		Context context = this.contextMap.get(classLoader);
		if (context == null)
			throw new IllegalArgumentException("Context for class loader" + classLoader + " isn't created");
		return context;
	}

	@NotNull
	@Override
	public LoggerFactory getLoggerFactory(@NotNull ClassLoader classLoader) {
		Objects.requireNonNull(classLoader, "classLoader is null");
		return this.getContext(classLoader).getLoggerFactory();
	}

	@NotNull
	@Override
	public LogConfigManager getConfigManager(@NotNull ClassLoader classLoader) {
		Objects.requireNonNull(classLoader, "classLoader is null");
		return this.getContext(classLoader).getConfigManager();
	}

	@Override
	public void bindClassLoader(@NotNull ClassLoader classLoader) {
		Objects.requireNonNull(classLoader, "classLoader is null");
		this.contextMap.computeIfAbsent(classLoader, Context::new);
		LOGGER.debug("Bind class loader {}", classLoader);
	}

	@Override
	public void unbindClassLoader(@NotNull ClassLoader classLoader) {
		Objects.requireNonNull(classLoader, "classLoader is null");
		Context context = this.contextMap.remove(classLoader);
		if (context != null)
			context.free();

		LOGGER.debug("Unbind class loader {}", classLoader);
	}

	@Override
	public void setDefaultStdout(@NotNull PrintStream stdout) {

	}

	@Override
	public void setDefaultStderr(@NotNull PrintStream stderr) {

	}

	// -------------------------------------------------------------------------------------------------------------- //

	private static class Context {
		@NotNull
		private static Configuration loadConfigFile(@NotNull LoggerContext loggerContext) {
			URL configFile = CONFIG_FILE.get();
			if (configFile == null) return new NullConfiguration();

			try (InputStream stream = configFile.openStream()) {
				return new XmlConfiguration(loggerContext, new ConfigurationSource(stream));
			} catch (IOException e) {
				LOGGER.error("Cannot read " + CONFIG_NAME, e);
				return new NullConfiguration();
			}
		}

		private final LoggerContext loggerContext;

		private final Log4jLoggerFactory loggerFactory;
		private final Log4jLogConfigManager configManager;

		public Context(@NotNull ClassLoader classLoader) {
			Objects.requireNonNull(classLoader, "classLoader is null");

			LOGGER.info("Create context from class loader {}", classLoader.getName());

			this.loggerContext = new LoggerContext("Context_" + classLoader.getName());
			this.loggerContext.setExternalContext(classLoader);
			this.loggerContext.start(loadConfigFile(this.loggerContext));

			this.loggerFactory = new Log4jLoggerFactory(this.loggerContext);
			this.configManager = new Log4jLogConfigManager(this.loggerContext);
		}

		@NotNull
		public Log4jLoggerFactory getLoggerFactory() {
			return this.loggerFactory;
		}

		@NotNull
		public Log4jLogConfigManager getConfigManager() {
			return this.configManager;
		}

		public void free() {
			this.loggerFactory.clean();
			this.configManager.clean();

			this.loggerContext.setExternalContext(null);
			this.loggerContext.stop();
		}
	}
}
