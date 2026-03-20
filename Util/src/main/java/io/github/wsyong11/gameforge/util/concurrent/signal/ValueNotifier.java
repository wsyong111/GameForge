package io.github.wsyong11.gameforge.util.concurrent.signal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ValueNotifier<T> {
	private final Object lock;
	private volatile T value;

	public ValueNotifier() {
		this(null);
	}

	public ValueNotifier(@Nullable T value) {
		this.lock = new Notifier();
		this.value = value;
	}

	public T get() {
		synchronized (this.lock) {
			return this.value;
		}
	}

	public void awaitUntil(@NotNull Predicate<T> condition) throws InterruptedException {
		Objects.requireNonNull(condition, "condition is null");

		synchronized (this.lock) {
			while (!condition.test(this.value)) {
				this.lock.wait();
			}
		}
	}

	public boolean awaitUntil(@NotNull Predicate<T> condition, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
		Objects.requireNonNull(condition, "condition is null");
		Objects.requireNonNull(unit, "unit is null");

		long deadline = System.nanoTime() + unit.toNanos(timeout);

		synchronized (this.lock) {
			while (!condition.test(this.value)) {
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

	@Nullable
	public T awaitChange() throws InterruptedException {
		synchronized (this.lock) {
			T old = this.value;
			while (this.equal(old, this.value)) {
				this.lock.wait();
			}
			return this.value;
		}
	}

	@Nullable
	public T update(@NotNull UnaryOperator<T> updater) {
		Objects.requireNonNull(updater, "updater is null");

		synchronized (this.lock) {
			T newValue = updater.apply(this.value);

			if (!this.equal(this.value, newValue)) {
				this.value = newValue;
				this.lock.notifyAll();
			}

			return this.value;
		}
	}

	public void set(T newValue) {
		synchronized (this.lock) {
			if (this.equal(this.value, newValue))
				return;

			this.value = newValue;
			this.lock.notifyAll();
		}
	}

	protected boolean equal(@Nullable T a, @Nullable T b) {
		return Objects.equals(a, b);
	}

	@Override
	public String toString() {
		return "ValueNotifier[" + this.value + "]";
	}
}
