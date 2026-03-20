package io.github.wsyong11.gameforge.util.concurrent.signal;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class Notifier {
	private final Object lock;
	private volatile int version;

	public Notifier() {
		this.lock = new Object();
		this.version = 0;
	}

	public int getVersion() {
		return this.version;
	}

	public void await() throws InterruptedException {
		synchronized (this.lock) {
			int currentVersion = this.version;
			while (this.version == currentVersion) {
				this.lock.wait();
			}
		}
	}

	public boolean await(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
		Objects.requireNonNull(unit, "unit is null");

		long deadline = System.nanoTime() + unit.toNanos(timeout);

		synchronized (this.lock) {
			int currentVersion = this.version;
			while (this.version == currentVersion) {
				long remaining = deadline - System.nanoTime();
				if (remaining <= 0)
					return false;

				long millis = remaining / 1_000_000;
				int nanos = (int) (remaining % 1_000_000);

				this.lock.wait(millis, nanos);
			}
			return true;
		}
	}

	public void await(@NotNull BooleanSupplier condition) throws InterruptedException {
		Objects.requireNonNull(condition, "condition is null");

		synchronized (this.lock) {
			while (!condition.getAsBoolean()) {
				this.lock.wait();
			}
		}
	}

	public boolean await(@NotNull BooleanSupplier condition, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
		Objects.requireNonNull(condition, "condition is null");
		Objects.requireNonNull(unit, "unit is null");

		long deadline = System.nanoTime() + unit.toNanos(timeout);

		synchronized (this.lock) {
			while (!condition.getAsBoolean()) {
				long remaining = deadline - System.nanoTime();
				if (remaining <= 0)
					return false;

				long millis = remaining / 1_000_000;
				int nanos = (int) (remaining % 1_000_000);

				this.lock.wait(millis, nanos);
			}
			return true;
		}
	}

	public void signal() {
		synchronized (this.lock) {
			this.version++;
			this.lock.notifyAll();
		}
	}
}
