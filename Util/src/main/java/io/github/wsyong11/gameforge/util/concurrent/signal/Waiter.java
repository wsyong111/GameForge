package io.github.wsyong11.gameforge.util.concurrent.signal;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Waiter {
	private final Object instance;

	public Waiter() {
		this.instance = new Object();
	}

	public void await() throws InterruptedException {
		synchronized (this.instance) {
			this.instance.wait();
		}
	}

	public void await(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
		Objects.requireNonNull(unit, "unit is null");
		synchronized (this.instance) {
			this.instance.wait(unit.toMillis(timeout));
		}
	}

	public void signal() {
		synchronized (this.instance) {
			this.instance.notifyAll();
		}
	}
}
