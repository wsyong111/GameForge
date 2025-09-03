package io.github.wsyong11.gameforge.framework.system.log.core;

import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2.SystemPrintStreamManager;
import io.github.wsyong11.gameforge.framework.system.log.core.adapter.LogSystemAdapter;
import io.github.wsyong11.gameforge.framework.system.log.core.config.LogConfigManager;
import io.github.wsyong11.gameforge.framework.system.log.core.config.LoggerConfig;
import io.github.wsyong11.gameforge.framework.system.log.core.logger.LoggerFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public final class LogManager {
	private static LogSystemAdapter currentAdapter = null;

	public static void setAdapter(@Nullable String id) {
		if (id == null) {
			setAdapter((LogSystemAdapter) null);
			return;
		}

		ServiceLoader<LogSystemAdapter> adapters = ServiceLoader.load(LogSystemAdapter.class, LogManager.class.getClassLoader());

		LogSystemAdapter selectedAdapter = null;
		for (LogSystemAdapter adapter : adapters) {
			if (!id.equals(adapter.getId())) continue;

			selectedAdapter = adapter;
			break;
		}
		if (selectedAdapter == null)
			throw new IllegalArgumentException("Cannot find adapter by id \"" + id + "\"");

		setAdapter(selectedAdapter);
	}

	public static void setAdapter(@Nullable LogSystemAdapter adapter) {
		if (adapter == currentAdapter) return;
		currentAdapter = adapter;
		initAdapter();
	}

	@NotNull
	public static String getAdapterId() {
		assertAdapter();
		return currentAdapter.getId();
	}

	private static void assertAdapter() {
		if (currentAdapter == null)
			throw new IllegalStateException("There are currently no adapters available");
	}

	private static void initAdapter() {
		assertAdapter();
		currentAdapter.setDefaultStdout(defaultStdout);
		currentAdapter.setDefaultStderr(defaultStderr);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private static PrintStream defaultStdout = System.out;
	private static PrintStream defaultStderr = System.err;

	public static void setDefaultStdout(@NotNull PrintStream stdout) {
		Objects.requireNonNull(stdout, "stdout is null");
		defaultStdout = stdout;
		if (currentAdapter != null)
			currentAdapter.setDefaultStdout(stdout);
		SystemPrintStreamManager.setStdout(stdout);
	}

	@NotNull
	public static PrintStream getDefaultStdout() {
		return defaultStdout;
	}

	public static void setDefaultStderr(@NotNull PrintStream stderr) {
		Objects.requireNonNull(stderr, "stderr is null");
		defaultStderr = stderr;
		if (currentAdapter != null)
			currentAdapter.setDefaultStderr(stderr);
		SystemPrintStreamManager.setStderr(stderr);
	}

	@NotNull
	public static PrintStream getDefaultStderr() {
		return defaultStderr;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private static final Map<ClassLoader, LogManager> INSTANCES = new ConcurrentHashMap<>();

	@NotNull
	public static LogManager bind(@NotNull ClassLoader classLoader) {
		Objects.requireNonNull(classLoader, "classLoader is null");
		return INSTANCES.computeIfAbsent(classLoader, LogManager::new);
	}

	public static void unbind(@NotNull ClassLoader classLoader) {
		Objects.requireNonNull(classLoader, "classLoader is null");

		LogManager instance = INSTANCES.remove(classLoader);
		if (instance != null)
			instance.destroy();
	}

	@NotNull
	public static LogManager getInstance(@NotNull ClassLoader classLoader) {
		Objects.requireNonNull(classLoader, "classLoader is null");
		return INSTANCES.computeIfAbsent(classLoader, LogManager::new);
	}

	@Nullable
	public static LogManager getInstanceUnsafe(@NotNull ClassLoader classLoader) {
		Objects.requireNonNull(classLoader, "classLoader is null");
		return INSTANCES.get(classLoader);
	}

	private static void unbindAll() {
		for (LogManager instance : INSTANCES.values()) {
			try {
				instance.destroy();
			} catch (Exception e) {
				defaultStderr.printf("Error in instance %s\n%s\n", instance, ExceptionUtils.getStackTrace(e));
			}
		}
		INSTANCES.clear();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	static {
		Thread cleanerThread = new Thread(LogManager::unbindAll);
		cleanerThread.setName("LogSystemCleaner");
		Runtime.getRuntime().addShutdownHook(cleanerThread);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private final LogSystemAdapter adapter;
	private final ClassLoader classLoader;

	private final LoggerFactory loggerFactory;
	private final LogConfigManager configManager;

	private LogManager(@NotNull ClassLoader classLoader) {
		Objects.requireNonNull(classLoader, "classLoader is null");
		assertAdapter();
		this.adapter = currentAdapter;

		this.classLoader = classLoader;

		this.adapter.bindClassLoader(classLoader);

		this.loggerFactory = this.adapter.getLoggerFactory(classLoader);
		this.configManager = this.adapter.getConfigManager(classLoader);
	}

	private void destroy() {
		this.adapter.unbindClassLoader(this.classLoader);
	}

	@NotNull
	public Logger getLogger(@NotNull String name) {
		Objects.requireNonNull(name, "name is null");
		return this.loggerFactory.getLogger(name);
	}

	@NotNull
	public LoggerConfig getLoggerConfig(@NotNull String name) {
		Objects.requireNonNull(name, "name is null");
		return this.configManager.getConfig(name);
	}

	@NotNull
	public LoggerConfig getRootLoggerConfig() {
		return this.configManager.getRootConfig();
	}
}
