package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio;

import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoder;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.concurrent.LimitedCapacityBlockingQueue;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AudioDecodePool {
	private static final Logger LOGGER = Log.getLogger();

	private final ExecutorService poolExecutor;
	private final ScheduledExecutorService scheduledExecutor;

	private volatile boolean isShutdown;

	private final ScheduledFuture<?> schedulePoolFuture;

	public AudioDecodePool(
		int maxPoolSize,
		int queueMaxCapacity,
		long keepAliveTime,
		long schedulePoolDelay,
		@NotNull TimeUnit unit
	) {
		Objects.requireNonNull(unit, "unit is null");

		this.poolExecutor = new ThreadPoolExecutor(
			1,
			maxPoolSize,
			keepAliveTime,
			unit,
			new LimitedCapacityBlockingQueue<>(new PriorityBlockingQueue<>(queueMaxCapacity), queueMaxCapacity),
			new AudioDecodeThreadFactory(),
			new ThreadPoolExecutor.CallerRunsPolicy()
		);

		this.scheduledExecutor = new ScheduledThreadPoolExecutor(1);

		this.isShutdown = false;

		this.schedulePoolFuture = this.scheduledExecutor.scheduleWithFixedDelay(
			this::schedulePool,
			0,
			schedulePoolDelay,
			unit
		);
	}

	private void schedulePool() {
		try {

		} catch (Throwable e) {
			LOGGER.error("Uncaught exception when scheduling pool", e);
		}
	}

	@NotNull
	public DecodeTask schedule(@NotNull AudioDecoder decoder) {

	}

	public void shutdown(long timeout, @NotNull TimeUnit unit) {
		Objects.requireNonNull(unit, "unit is null");

		if (this.isShutdown)
			return;
		this.isShutdown = true;

		this.schedulePoolFuture.cancel(true);
		try {
			this.schedulePoolFuture.get(timeout, unit);
		} catch (CancellationException | TimeoutException ignored) {
		} catch (ExecutionException e) {
			LOGGER.warn("Schedule pool method uncaught exception", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		this.scheduledExecutor.shutdown();
		try {
			if (!this.scheduledExecutor.awaitTermination(timeout, unit))
				this.scheduledExecutor.shutdownNow();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		this.poolExecutor.shutdown();
		try {
			if (!this.poolExecutor.awaitTermination(timeout, unit))
				this.poolExecutor.shutdownNow();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static class AudioDecodeThreadFactory implements ThreadFactory {
		private final AtomicInteger id;

		public AudioDecodeThreadFactory() {
			this.id = new AtomicInteger(0);
		}

		@NotNull
		@Override
		public Thread newThread(@NotNull Runnable r) {
			Objects.requireNonNull(r, "r is null");

			Thread thread = new Thread(r);
			thread.setDaemon(false);
			thread.setName("AudioDecode - " + this.id.getAndIncrement());
			return thread;
		}
	}

	public static class DecodeTask {

	}
}
