package io.github.wsyong11.gameforge.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

@UtilityClass
public class FunctionUtils {
	public static <T> T run(@NotNull Supplier<T> supplier) {
		Objects.requireNonNull(supplier, "supplier is null");
		return supplier.get();
	}

	@NotNull
	public static <T> Callable<T> toCallable(@NotNull Runnable runnable, @Nullable T resultValue) {
		Objects.requireNonNull(runnable, "runnable is null");
		return () -> {
			runnable.run();
			return resultValue;
		};
	}

	@NotNull
	public static <T> Callable<T> toCallable(@NotNull Runnable runnable) {
		Objects.requireNonNull(runnable, "runnable is null");
		return toCallable(runnable, null);
	}
}
