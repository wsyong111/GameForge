package io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2;

import com.google.auto.service.AutoService;
import io.github.wsyong11.gameforge.framework.annotation.Internal;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2.plugin.LogDirContextProvider;
import io.github.wsyong11.gameforge.framework.system.log.core.adapter.LogSystemAdapter;
import io.github.wsyong11.gameforge.framework.system.log.core.config.LogConfigManager;
import io.github.wsyong11.gameforge.framework.system.log.core.logger.LoggerFactory;
import io.github.wsyong11.gameforge.framework.system.log.core.logger.NoopLogger;
import io.github.wsyong11.gameforge.util.Lazy;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.NullConfiguration;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;
import org.apache.logging.log4j.status.StatusLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Internal
@AutoService(LogSystemAdapter.class)
public class Log4jAdapter implements LogSystemAdapter {
	private static final String CONFIG_NAME = "log4j2.xml";

	private final Map<ClassLoader, Context> contextMap;

	private final Lazy<URL> configFile;

	private volatile Path logDir;
	private volatile boolean debug;
	private volatile Logger logger;

	public Log4jAdapter() {
		this.contextMap = new ConcurrentHashMap<>();

		this.configFile = Lazy.concurrentOf(() -> {
			ClassLoader classLoader = ClassLoader.getSystemClassLoader();
			URL resource = classLoader.getResource(CONFIG_NAME);
			if (resource == null)
				this.logger.warn("Configuration file not found {}", CONFIG_NAME);
			return resource;
		});

		this.logDir = Path.of("log");
		this.debug = false;
		this.logger = NoopLogger.INSTANCE;
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

		this.contextMap.computeIfAbsent(classLoader, this::createContext);
		this.logger.debug("Bind class loader {}", classLoader);
	}

	@NotNull
	private Context createContext(@NotNull ClassLoader classLoader) {
		URL configFile = this.configFile.get();
		return new Context(configFile, classLoader);
	}

	@Override
	public void unbindClassLoader(@NotNull ClassLoader classLoader) {
		Objects.requireNonNull(classLoader, "classLoader is null");

		Context context = this.contextMap.remove(classLoader);
		if (context != null)
			context.free();

		this.logger.debug("Unbind class loader {}", classLoader);
	}

	@Override
	public void setDefaultStdout(@NotNull PrintStream stdout) {
		this.logger.debug("Set default stdout to {}", stdout);
	}

	@Override
	public void setDefaultStderr(@NotNull PrintStream stderr) {
		this.logger.debug("Set default stderr to {}", stderr);
	}

	@Override
	public void init() {
		LogDirContextProvider.setAdapter(this);
	}

	@Override
	public void destroy() {
		LogDirContextProvider.setAdapter(null);
	}

	@Override
	public void setLogDir(@NotNull Path logDir) {
		Objects.requireNonNull(logDir, "logDir is null");
		this.logDir = logDir;
	}

	@NotNull
	public String getLogDir() {
		return this.logDir.toAbsolutePath().toString();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public void setDebug(boolean enable) {
		this.debug = enable;
		this.logger = enable
			? new Log4jLogger(StatusLogger.getLogger())
			: NoopLogger.INSTANCE;
	}

	public boolean isDebug() {
		return this.debug;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private class Context {

		private final LoggerContext loggerContext;

		private final Log4jLoggerFactory loggerFactory;
		private final Log4jLogConfigManager configManager;

		public Context(@Nullable URL configFile, @NotNull ClassLoader classLoader) {
			Objects.requireNonNull(classLoader, "classLoader is null");

			logger.info("Create context from class loader {}", classLoader.getName());

			this.loggerContext = new LoggerContext("Context_" + classLoader.getName());
			this.loggerContext.setExternalContext(classLoader);
			this.loggerContext.start(loadConfigFile(configFile));

			this.loggerFactory = new Log4jLoggerFactory(this.loggerContext);
			this.configManager = new Log4jLogConfigManager(this.loggerContext);
		}

		@NotNull
		private Configuration loadConfigFile(@Nullable URL configFile) {
			if (configFile == null)
				return new NullConfiguration();

			try (InputStream stream = configFile.openStream()) {
				return new XmlConfiguration(this.loggerContext, new ConfigurationSource(stream));
			} catch (IOException e) {
				logger.error("Cannot read {}", CONFIG_NAME, e);
				return new NullConfiguration();
			}
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
