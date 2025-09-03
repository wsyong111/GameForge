package io.github.wsyong11.gameforge.framework.system.log;

import io.github.wsyong11.gameforge.framework.annotation.CallerSensitive;
import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;
import io.github.wsyong11.gameforge.framework.system.log.core.logger.LazyLoadLoggerFactory;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@UtilityClass
public class Log {
	private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

	@NotNull
	@CallerSensitive
	public static Logger getLogger() {
		return getLogger(STACK_WALKER.getCallerClass());
	}

	@NotNull
	public static Logger getLogger(@NotNull Class<?> clazz) {
		Objects.requireNonNull(clazz, "clazz is null");
		return getLogger(clazz.getClassLoader(), clazz.getName());
	}

	@NotNull
	@CallerSensitive
	public static Logger getLogger(@NotNull String name) {
		Objects.requireNonNull(name, "name is null");

		Class<?> clazz = STACK_WALKER.getCallerClass();
		return getLogger(clazz.getClassLoader(), name);
	}

	@NotNull
	public static Logger getLogger(@NotNull ClassLoader classLoader, @NotNull String name) {
		Objects.requireNonNull(name, "name is null");
		Objects.requireNonNull(classLoader, "classLoader is null");

		LogManager logManager = LogManager.getInstanceUnsafe(classLoader);
		if (logManager == null)
			// Get lazy noop logger, until LogManager has been initialized
			return LazyLoadLoggerFactory.get(classLoader, name);
		else
			return logManager.getLogger(name);
	}
}
