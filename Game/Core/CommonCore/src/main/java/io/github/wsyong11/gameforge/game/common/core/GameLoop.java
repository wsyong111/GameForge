package io.github.wsyong11.gameforge.game.common.core;

import io.github.wsyong11.gameforge.framework.annotation.ThreadSensitive;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.game.common.tick.Tickable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class GameLoop {
	private static final Logger LOGGER = Log.getLogger();

	private final Tickable tickable;

	private final Watchdog watchdog;

	private int tps;
	private long tickIntervalNs;

	private volatile boolean stopping;
	private volatile Thread thread;

	public GameLoop(int tps, @NotNull Tickable tickable) {
		Objects.requireNonNull(tickable, "tickable is null");

		this.tickable = tickable;

		this.watchdog = new Watchdog(
			this::processTimeout,
			Long.MAX_VALUE,
			Long.MAX_VALUE,
			TimeUnit.SECONDS
		);

		this.setTps(tps);

		this.stopping = false;
		this.thread = null;
	}

	public int getTps() {
		return this.tps;
	}

	public void setTps(int tps) {
		if (tps <= 0)
			throw new IllegalArgumentException("Tps cannot be negative or zero, tps = " + tps);

		if (this.tps == tps)
			return;

		this.tps = tps;

		long tickIntervalMs = 1000L / tps;
		this.tickIntervalNs = TimeUnit.MILLISECONDS.toNanos(tickIntervalMs);

		this.watchdog.setWarningElapse(tickIntervalMs + 1000L, TimeUnit.MILLISECONDS);
		this.watchdog.setMaxElapse(tickIntervalMs + 10_000L, TimeUnit.MILLISECONDS);
	}

	protected void processTimeout() {
		this.stop();
	}

	public void stop() {
		if (this.thread == null)
			throw new IllegalStateException("Game loop is not run");

		if (this.stopping)
			return;

		LOGGER.debug("Stopping game loop");
		this.stopping = true;
		LockSupport.unpark(this.thread);
	}

	@ThreadSensitive
	public boolean run() {
		if (this.thread != null)
			throw new IllegalStateException("Game loop is running");

		if (this.stopping)
			return false;

		this.thread = Thread.currentThread();

		this.watchdog.start();

		LOGGER.info("Start game loop");

		try {
			while (!this.stopping) {
				this.watchdog.tick();

				long startNs = System.nanoTime();

				try {
					this.tickable.tick();
				} catch (Throwable t) {
					LOGGER.error("Tick error", t);
					return false;
				}

				long elapsedNs = System.nanoTime() - startNs;

				long waitTimeNs = this.tickIntervalNs - elapsedNs;
				if (waitTimeNs > 0)
					LockSupport.parkNanos(waitTimeNs);
			}
		} finally {
			this.watchdog.exit();
			LOGGER.info("Game loop stopped");
		}

		return true;
	}
}
