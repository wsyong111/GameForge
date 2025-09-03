package io.github.wsyong11.gameforge.framework.system.log.core.logger;

import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NoopLogger implements Logger {
	public static final NoopLogger INSTANCE = new NoopLogger();

	private NoopLogger() { /* no-op */ }

	@NotNull
	@Override
	public String getName() {
		return "";
	}

	@Override
	public boolean isLevelEnabled(@NotNull LogLevel level) {
		return false;
	}

	@Override
	public boolean isTraceEnabled() {
		return false;
	}

	@Override
	public void trace(@NotNull String message) { /* no-op */ }

	@Override
	public void trace(@NotNull String message, @Nullable Object arg0) { /* no-op */ }

	@Override
	public void trace(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1) { /* no-op */ }

	@Override
	public void trace(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2) { /* no-op */ }

	@Override
	public void trace(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3) { /* no-op */ }

	@Override
	public void trace(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4) { /* no-op */ }

	@Override
	public void trace(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5) { /* no-op */ }

	@Override
	public void trace(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6) { /* no-op */ }

	@Override
	public void trace(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7) { /* no-op */ }

	@Override
	public void trace(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7, @Nullable Object arg8) { /* no-op */ }

	@Override
	public void trace(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7, @Nullable Object arg8, @Nullable Object arg9) { /* no-op */ }

	@Override
	public void trace(@NotNull String message, @Nullable Object... args) { /* no-op */ }

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public void debug(@NotNull String message) { /* no-op */ }

	@Override
	public void debug(@NotNull String message, @Nullable Object arg0) { /* no-op */ }

	@Override
	public void debug(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1) { /* no-op */ }

	@Override
	public void debug(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2) { /* no-op */ }

	@Override
	public void debug(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3) { /* no-op */ }

	@Override
	public void debug(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4) { /* no-op */ }

	@Override
	public void debug(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5) { /* no-op */ }

	@Override
	public void debug(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6) { /* no-op */ }

	@Override
	public void debug(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7) { /* no-op */ }

	@Override
	public void debug(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7, @Nullable Object arg8) { /* no-op */ }

	@Override
	public void debug(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7, @Nullable Object arg8, @Nullable Object arg9) { /* no-op */ }

	@Override
	public void debug(@NotNull String message, @Nullable Object... args) { /* no-op */ }

	@Override
	public boolean isInfoEnabled() {
		return false;
	}

	@Override
	public void info(@NotNull String message) { /* no-op */ }

	@Override
	public void info(@NotNull String message, @Nullable Object arg0) { /* no-op */ }

	@Override
	public void info(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1) { /* no-op */ }

	@Override
	public void info(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2) { /* no-op */ }

	@Override
	public void info(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3) { /* no-op */ }

	@Override
	public void info(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4) { /* no-op */ }

	@Override
	public void info(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5) { /* no-op */ }

	@Override
	public void info(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6) { /* no-op */ }

	@Override
	public void info(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7) { /* no-op */ }

	@Override
	public void info(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7, @Nullable Object arg8) { /* no-op */ }

	@Override
	public void info(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7, @Nullable Object arg8, @Nullable Object arg9) { /* no-op */ }

	@Override
	public void info(@NotNull String message, @Nullable Object... args) { /* no-op */ }

	@Override
	public boolean isWarnEnabled() {
		return false;
	}

	@Override
	public void warn(@NotNull String message) { /* no-op */ }

	@Override
	public void warn(@NotNull String message, @Nullable Object arg0) { /* no-op */ }

	@Override
	public void warn(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1) { /* no-op */ }

	@Override
	public void warn(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2) { /* no-op */ }

	@Override
	public void warn(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3) { /* no-op */ }

	@Override
	public void warn(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4) { /* no-op */ }

	@Override
	public void warn(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5) { /* no-op */ }

	@Override
	public void warn(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6) { /* no-op */ }

	@Override
	public void warn(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7) { /* no-op */ }

	@Override
	public void warn(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7, @Nullable Object arg8) { /* no-op */ }

	@Override
	public void warn(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7, @Nullable Object arg8, @Nullable Object arg9) { /* no-op */ }

	@Override
	public void warn(@NotNull String message, @Nullable Object... args) { /* no-op */ }

	@Override
	public boolean isErrorEnabled() {
		return false;
	}

	@Override
	public void error(@NotNull String message) { /* no-op */ }

	@Override
	public void error(@NotNull String message, @Nullable Object arg0) { /* no-op */ }

	@Override
	public void error(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1) { /* no-op */ }

	@Override
	public void error(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2) { /* no-op */ }

	@Override
	public void error(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3) { /* no-op */ }

	@Override
	public void error(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4) { /* no-op */ }

	@Override
	public void error(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5) { /* no-op */ }

	@Override
	public void error(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6) { /* no-op */ }

	@Override
	public void error(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7) { /* no-op */ }

	@Override
	public void error(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7, @Nullable Object arg8) { /* no-op */ }

	@Override
	public void error(@NotNull String message, @Nullable Object arg0, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4, @Nullable Object arg5, @Nullable Object arg6, @Nullable Object arg7, @Nullable Object arg8, @Nullable Object arg9) { /* no-op */ }

	@Override
	public void error(@NotNull String message, @Nullable Object... args) { /* no-op */ }
}
