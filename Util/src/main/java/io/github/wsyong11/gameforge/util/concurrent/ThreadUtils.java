package io.github.wsyong11.gameforge.util.concurrent;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@UtilityClass
public class ThreadUtils {
	@NotNull
	public static <T> T waitValue(@NotNull Object lock, long timeoutMs, @NotNull Supplier<T> supplier) throws InterruptedException, TimeoutException {
		Objects.requireNonNull(lock, "lock is null");
		Objects.requireNonNull(supplier, "supplier is null");

		long startTimeNs = System.nanoTime();
		while (true) {
			T value = supplier.get();
			if (value != null)
				return value;

			if (timeoutMs > 0L) {
				long elapsedTimeMs = (System.nanoTime() - startTimeNs) / 1000L / 1000L;
				long remainingMs = timeoutMs - elapsedTimeMs;
				if (remainingMs <= 0)
					throw new TimeoutException();

				lock.wait(remainingMs);
			} else {
				lock.wait();
			}
		}
	}
}
