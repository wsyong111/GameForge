package io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2;

import io.github.wsyong11.gameforge.framework.annotation.UnsafeAPI;
import io.github.wsyong11.gameforge.util.io.PrintStreamWrapper;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Objects;

@UtilityClass
@UnsafeAPI
public class SystemPrintStreamManager {
	private static final PrintStreamWrapper stdoutWrapper = new PrintStreamWrapper(System.out);
	private static final PrintStreamWrapper stderrWrapper = new PrintStreamWrapper(System.err);

	public static void setStdout(@NotNull PrintStream stdout) {
		Objects.requireNonNull(stdout, "stdout is null");
		stdoutWrapper.setImpl(stdout);
	}

	@NotNull
	public static PrintStream getStdout() {
		PrintStream impl = stdoutWrapper.getImpl();
		assert impl != null : "Stdout impl is null";
		return impl;
	}
	public static void setStderr(@NotNull PrintStream stderr) {
		Objects.requireNonNull(stderr, "stderr is null");
		stderrWrapper.setImpl(stderr);
	}

	@NotNull
	public static PrintStream getStderr() {
		PrintStream impl = stderrWrapper.getImpl();
		assert impl != null : "Stdout impl is null";
		return impl;
	}

	@NotNull
	public static PrintStream getWrappedStdout() {
		return stdoutWrapper;
	}

	@NotNull
	public static PrintStreamWrapper getWrappedStderr() {
		return stderrWrapper;
	}

	static {
		System.setOut(stdoutWrapper);
		System.setOut(stderrWrapper);
	}
}
