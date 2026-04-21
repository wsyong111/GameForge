package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

public class MultiThreadProcessor<I, O> {
	private final ExecutorService executor;

	public MultiThreadProcessor(
		int maxThreadCount,
		long keepAliveTime,
		@NotNull TimeUnit unit,
		@NotNull ThreadFactory threadFactory
	) {
		this.executor = new ThreadPoolExecutor(
			1,
			maxThreadCount,
			keepAliveTime,
			unit,
			new ArrayBlockingQueue<>(maxThreadCount + 8),
			threadFactory
		);
	}

	public void shutdown(@NotNull)
}
