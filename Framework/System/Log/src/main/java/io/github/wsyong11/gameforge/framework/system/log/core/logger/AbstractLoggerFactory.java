package io.github.wsyong11.gameforge.framework.system.log.core.logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractLoggerFactory implements LoggerFactory {
	private final LoadingCache<String, Logger> loggerCache;

	protected AbstractLoggerFactory() {
		this.loggerCache = CacheBuilder
			.newBuilder()
			.softValues()
			.build(new CacheLoader<>() {
				@NotNull
				@Override
				public Logger load(@NotNull String key) {
					Objects.requireNonNull(key, "key is null");
					return createLogger(key);
				}
			});
	}

	@NotNull
	@Override
	public Logger getLogger(@NotNull String name) {
		Objects.requireNonNull(name, "name is null");
		return this.loggerCache.getUnchecked(name);
	}

	public void clean() {
		this.loggerCache.invalidateAll();
	}

	@NotNull
	protected abstract Logger createLogger(@NotNull String name);
}
