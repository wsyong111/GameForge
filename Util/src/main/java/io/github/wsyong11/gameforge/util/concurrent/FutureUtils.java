package io.github.wsyong11.gameforge.util.concurrent;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.*;

@UtilityClass
public class FutureUtils {
	public static boolean cancelAwait(@Nullable Future<?> future, long timeout, @NotNull TimeUnit unit) {
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
}
