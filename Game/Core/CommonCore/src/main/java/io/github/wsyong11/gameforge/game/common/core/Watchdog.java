package io.github.wsyong11.gameforge.game.common.core;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Watchdog extends Thread {
	private static final Logger LOGGER = Log.getLogger();

	private final Runnable callback;

	private long lastTickTimeNs;
	private long warningElapseNs;
	private long maxElapseNs;

	public Watchdog(@NotNull Runnable callback, long warningElapse, long maxElapse, @NotNull TimeUnit unit) {
		Objects.requireNonNull(callback, "callback is null");
		Objects.requireNonNull(unit, "unit is null");

		this.callback = callback;
		this.warningElapseNs=unit.toNanos(warningElapse);
		this.maxElapseNs=unit.toNanos(maxElapse);

		this.lastTickTimeNs = 0L;

		this.setName("Watchdog");
		this.setDaemon(true);
	}

	public void tick() {
		this.lastTickTimeNs = System.nanoTime();
	}

	public void setWarningElapse(long warningElapse, @NotNull TimeUnit unit) {
		Objects.requireNonNull(unit, "unit is null");

		long warningElapseNs = unit.toNanos(warningElapse);
		if (this.warningElapseNs == warningElapseNs)
			return;

		this.warningElapseNs = warningElapseNs;
		LockSupport.unpark(this);
	}

	public void setMaxElapse(long maxElapse, @NotNull TimeUnit unit) {
		Objects.requireNonNull(unit, "unit is null");

		long maxElapseNs = unit.toNanos(maxElapse);
		if (this.maxElapseNs == maxElapseNs)
			return;

		this.maxElapseNs = maxElapseNs;
		LockSupport.unpark(this);
	}

	@Override
	public synchronized void start() {
		if (this.getState() != State.NEW)
			return;

		this.tick();
		super.start();
	}

	@Override
	public void run() {
		boolean warned = false;
		boolean callbackInvoked = false;

		while (!this.isInterrupted()) {
			long elapseNs = System.nanoTime() - this.lastTickTimeNs;

			if (!warned && elapseNs > this.warningElapseNs) {
				warned = true;
				LOGGER.warn("Logic thread unresponsive for {} ms",
					TimeUnit.NANOSECONDS.toMillis(elapseNs));
			}

			if (elapseNs > this.maxElapseNs) {
				if (!callbackInvoked) {
					LOGGER.error("Logic thread exceeded max allowed {} ms (actual {} ms)",
						TimeUnit.NANOSECONDS.toMillis(this.maxElapseNs),
						TimeUnit.NANOSECONDS.toMillis(elapseNs));

					callbackInvoked = true;
					try {
						this.callback.run();
					} catch (Throwable e) {
						LOGGER.error("Callback exception", e);
					}
				}

				LockSupport.parkNanos(100L * 1000L * 1000L);
				continue;
			}

			if (elapseNs < this.warningElapseNs) {
				warned = false;
			}

			callbackInvoked = false;

			LockSupport.parkNanos(10L * 1000L * 1000L);
		}
	}

	public void exit() {
		if (!this.isAlive())
			return;

		this.interrupt();
		LockSupport.unpark(this);
	}
}
