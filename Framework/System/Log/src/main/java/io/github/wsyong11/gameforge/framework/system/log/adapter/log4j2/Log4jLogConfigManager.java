package io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2;

import io.github.wsyong11.gameforge.framework.annotation.Internal;
import io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2.config.Log4jLoggerConfig;
import io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2.config.UpdateCallback;
import io.github.wsyong11.gameforge.framework.system.log.core.config.LogConfigManager;
import io.github.wsyong11.gameforge.framework.system.log.core.config.LoggerConfig;
import io.github.wsyong11.gameforge.util.Lazy;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Internal
public class Log4jLogConfigManager implements LogConfigManager {
	private final Configuration configuration;
	private final UpdateCallback updateCallback;

	private final Lazy<Log4jLoggerConfig> rootConfig;
	private final LoadingCache<String, LoggerConfig> configCache;

	public Log4jLogConfigManager(@NotNull LoggerContext context) {
		Objects.requireNonNull(context, "context is null");

		this.configuration = context.getConfiguration();
		this.updateCallback = context::updateLoggers;

		this.rootConfig = Lazy.concurrentOf(() ->
			new Log4jLoggerConfig(this.configuration.getRootLogger(), this.updateCallback));

		this.configCache = CacheBuilder
			.newBuilder()
			.softValues()
			.build(new CacheLoader<>() {
				@NotNull
				@Override
				public LoggerConfig load(@NotNull String key) {
					Objects.requireNonNull(key, "key is null");
					return createConfig(key);
				}
			});
	}

	@NotNull
	private LoggerConfig createConfig(@NotNull String name) {
		Objects.requireNonNull(name, "name is null");
		return new Log4jLoggerConfig(this.configuration.getLoggerConfig(name), this.updateCallback);
	}

	@NotNull
	@Override
	public LoggerConfig getRootConfig() {
		return this.rootConfig.get();
	}

	@NotNull
	@Override
	public LoggerConfig getConfig(@NotNull String name) {
		Objects.requireNonNull(name, "name is null");
		return this.configCache.getUnchecked(name);
	}

	public void clean() {
		this.configCache.invalidateAll();
	}
}
