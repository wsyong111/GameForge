package io.github.wsyong11.gameforge.framework.system.log.core.logger;

import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

public interface LoggerFactory {
	@NotNull
	Logger getLogger(@NotNull String name);
}
