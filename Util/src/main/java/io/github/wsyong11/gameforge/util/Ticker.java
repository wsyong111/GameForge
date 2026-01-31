package io.github.wsyong11.gameforge.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public abstract class Ticker {
	@NotNull
	public static Ticker loop(int tickPreSec, @NotNull TickCallback callback) {
		Objects.requireNonNull(callback, "callback is null");
		return new LoopTicker(tickPreSec, callback);
	}

	@NotNull
	public static Ticker accumulator(int tickPreSec, @NotNull TickCallback callback) {
		Objects.requireNonNull(callback, "callback is null");
		return new AccumulatorTicker(tickPreSec, callback);
	}

	protected final TickCallback callback;

	protected volatile long tickDurationNs;
	private volatile boolean stopped;
	private volatile Thread loopThread;

	public Ticker(int tickPreSec, @NotNull TickCallback callback) {
		Objects.requireNonNull(callback, "callback is null");

		this.callback = callback;

		this.tickDurationNs = 0L;
		this.setTickSpeed(tickPreSec);

		this.stopped = true;
		this.loopThread = null;
	}

	public void setTickSpeed(int tickPreSec) {
		if (tickPreSec <= 0)
			throw new IllegalArgumentException("Tick pre-seconds is negative or zero");

		this.tickDurationNs = 1_000_000_000L / tickPreSec;
	}

	public long getTickPeriod(@NotNull TimeUnit unit) {
		Objects.requireNonNull(unit, "unit is null");
		return TimeUnit.NANOSECONDS.convert(this.tickDurationNs, unit);
	}

	public void loop() {
		synchronized (this) {
			if (this.loopThread != null)
				throw new IllegalStateException("A loop is already running");

			this.stopped = false;
			this.loopThread = Thread.currentThread();
		}

		try {
			this.mainLoop();
		} finally {
			synchronized (this) {
				this.stopped = true;
				this.loopThread = null;
			}
		}
	}

	public void stop() {
		synchronized (this) {
			this.stopped = true;

			Thread loopThread = this.loopThread;
			if (loopThread != null)
				LockSupport.unpark(loopThread);
		}
	}

	public boolean isRunning() {
		return !this.stopped;
	}

	protected abstract void mainLoop();

	@FunctionalInterface
	public interface TickCallback {
		void tick(double dtMs);
	}

	private static class LoopTicker extends Ticker {
		public LoopTicker(int tickPreSec, @NotNull TickCallback callback) {
			super(tickPreSec, callback);
		}

		@Override
		public void mainLoop() {
			long last = System.nanoTime();
			while (this.isRunning()) {
				long now = System.nanoTime();
				long elapsed = now - last;
				last = now;

				this.callback.tick(elapsed / 1_000_000.0D);

				long sleep = this.tickDurationNs - elapsed;
				if (sleep > 0)
					LockSupport.parkNanos(sleep);
			}
		}
	}

	private static class AccumulatorTicker extends Ticker {
		public AccumulatorTicker(int tickPreSec, @NotNull TickCallback callback) {
			super(tickPreSec, callback);
		}

		@Override
		protected void mainLoop() {
			long last = System.nanoTime();
			long accumulator = 0;

			while (this.isRunning()) {
				long now = System.nanoTime();
				long elapsed = now - last;
				last = now;

				accumulator += elapsed;

				if (accumulator >= this.tickDurationNs * 5)
					accumulator = this.tickDurationNs * 5;

				while (accumulator >= this.tickDurationNs) {
					this.callback.tick(this.tickDurationNs / 1_000_000.0);
					accumulator -= this.tickDurationNs;
				}

				long sleep = this.tickDurationNs - accumulator;
				if (sleep > 0)
					LockSupport.parkNanos(sleep);
			}
		}
	}
}
