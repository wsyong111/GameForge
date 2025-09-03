package io.github.wsyong11.gameforge.util.concurrent.signal;

public class ThreadSignal {
	private final Object signal;
	private volatile boolean set;

	public ThreadSignal() {
		this.signal = new Object();
		this.set = false;
	}

	public void set() {
		if (this.set) return;

		synchronized (this.signal) {
			if (this.set)
				return;

			this.set = true;
			this.signal.notifyAll();
		}
	}

	public void reset() {
		if (!this.set) return;

		synchronized (this.signal) {
			if (!this.set) return;
			this.set = false;
		}
	}

	public void await() throws InterruptedException {
		synchronized (this.signal) {
			while (!this.set)
				this.signal.wait();
		}
	}

	public boolean await(long timeoutMs) throws InterruptedException {
		if (timeoutMs <= 0)
			throw new IllegalArgumentException("Timeout is zero or negative");

		synchronized (this.signal) {
			long deadline = System.nanoTime() + timeoutMs * 1_000_000L;
			long remaining = timeoutMs;
			while (!this.set && remaining > 0) {
				this.signal.wait(remaining / 1_000_000L, (int) (remaining % 1_000_000L));
				remaining = deadline - System.nanoTime();
			}
			return this.set;
		}
	}

	public void awaitAndReset() throws InterruptedException {
		synchronized (this.signal) {
			while (!this.set)
				this.signal.wait();
			this.set = false;
		}
	}

	public boolean isSet() {
		return this.set;
	}
}
