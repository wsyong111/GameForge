package io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2;

import io.github.wsyong11.gameforge.framework.annotation.Internal;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.core.logger.AbstractLoggerFactory;
import org.apache.logging.log4j.spi.LoggerContext;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Internal
public class Log4jLoggerFactory extends AbstractLoggerFactory {
	private final LoggerContext context;

	public Log4jLoggerFactory(@NotNull LoggerContext context) {
		Objects.requireNonNull(context, "context is null");
		this.context = context;
	}

	@NotNull
	@Override
	protected Logger createLogger(@NotNull String name) {
		Objects.requireNonNull(name, "name is null");
		return new Log4jLogger(this.context.getLogger(name));
	}
}
