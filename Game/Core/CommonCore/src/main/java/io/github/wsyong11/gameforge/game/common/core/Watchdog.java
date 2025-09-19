package io.github.wsyong11.gameforge.game.common.core;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.locks.LockSupport;

public class Watchdog extends Thread {
	private static final Logger LOGGER = Log.getLogger();

	private final Runnable callback;

	private long lastTickTimeNs;
	private long warningElapseNs = 1000L * 1000L * 1000L;
	private long maxElapseNs = 1000L * 1000L * 1000L;

	public Watchdog(@NotNull Runnable callback) {
		Objects.requireNonNull(callback, "callback is null");

		this.callback = callback;

		this.lastTickTimeNs = 0L;

		this.setName("Watchdog");
		this.setDaemon(true);
	}

	public void tick() {
		this.lastTickTimeNs = System.nanoTime();
	}

	@Override
	public synchronized void start() {
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
					elapseNs / 1000L / 1000L);
			}

			if (elapseNs > this.maxElapseNs) {
				if (!callbackInvoked) {
					LOGGER.error("Logic thread exceeded max allowed {} ms (actual {} ms)",
						this.maxElapseNs / 1000L / 1000L,
						elapseNs / 1000L / 1000L);

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
