package io.github.wsyong11.gameforge.util.concurrent;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class ExecutorServiceUtils {
	public static void shutdown(@NotNull ExecutorService executor, long timeout, @NotNull TimeUnit unit) {
		Objects.requireNonNull(executor, "executor is null");
		Objects.requireNonNull(unit, "unit is null");

		executor.shutdown();
		try {
			if (executor.awaitTermination(timeout, unit))
				executor.shutdownNow();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			executor.shutdownNow();
		}
	}
}
