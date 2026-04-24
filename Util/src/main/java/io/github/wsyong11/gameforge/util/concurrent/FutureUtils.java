package io.github.wsyong11.gameforge.util.concurrent;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.*;

@UtilityClass
public class FutureUtils {
	public static boolean cancelAwait(@Nullable Future<?> future, long timeout, @NotNull TimeUnit unit) {
		Objects.requireNonNull(unit, "unit is null");

		if (future == null)
			return true;

		future.cancel(true);
		try {
			future.get(timeout, unit);
		} catch (ExecutionException | CancellationException ignored) {
			/* no-op */
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		} catch (TimeoutException e) {
			return false;
		}

		return true;
	}

	public static boolean isDoneNormal(@NotNull Future<?> future) {
		Objects.requireNonNull(future, "future is null");
		return future.isDone() && getException(future) == null;
	}

	@Nullable
	public static Throwable getException(@NotNull Future<?> future) {
		Objects.requireNonNull(future, "future is null");

		if (!future.isDone())
			return null;

		try {
			future.get();
		} catch (ExecutionException e) {
			return e.getCause();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (CancellationException ignored) {
		}

		return null;
	}
}
