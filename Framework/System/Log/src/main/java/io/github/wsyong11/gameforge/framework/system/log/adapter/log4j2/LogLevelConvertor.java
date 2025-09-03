package io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2;

import io.github.wsyong11.gameforge.framework.annotation.Internal;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@Internal
@UtilityClass
public class LogLevelConvertor {
	@Contract("null -> null; !null -> !null")
	@Nullable
	public static Level toLog4jLevel(@Nullable LogLevel level) {
		if (level == null)
			return null;

		return switch (level) {
			case TRACE -> Level.TRACE;
			case DEBUG -> Level.DEBUG;
			case INFO -> Level.INFO;
			case WARN -> Level.WARN;
			case ERROR -> Level.ERROR;
		};
	}

	@Contract("null -> null; !null -> !null")
	@Nullable
	public static LogLevel toLevel(@Nullable Level level) {
		if (level == null)
			return null;

		if (level == Level.TRACE) return LogLevel.TRACE;
		if (level == Level.DEBUG) return LogLevel.DEBUG;
		if (level == Level.INFO) return LogLevel.INFO;
		if (level == Level.WARN) return LogLevel.WARN;
		if (level == Level.ERROR) return LogLevel.ERROR;
		throw new IllegalArgumentException();
	}
}
