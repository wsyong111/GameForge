package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio;

import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.util.concurrent.LimitedCapacityBlockingQueue;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AudioDecoderPool {
	private final ExecutorService executor;

	public AudioDecoderPool(
		int maxCapacity,
		int maxPoolSize,
		long keepAliveTime,
		@NotNull TimeUnit unit
	) {
		Objects.requireNonNull(unit, "unit is null");
		this.executor = new ThreadPoolExecutor(
			1,
			maxPoolSize,
			keepAliveTime,
			unit,
			new LimitedCapacityBlockingQueue<>(new PriorityBlockingQueue<>(maxCapacity), maxCapacity),
			new AudioDecoderThreadFactory(),
			new ThreadPoolExecutor.CallerRunsPolicy()
		);
	}

	@NotNull
	public Audio decode(int priority, @NotNull InputStream dataStream) {
		Objects.requireNonNull(dataStream, "dataStream is null");


	}

	public void shutdown() {
		this.executor.shutdown();
		try {
			if (!this.executor.awaitTermination(5, TimeUnit.SECONDS)) {
				this.executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			this.executor.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	protected static class DecoderTask implements Callable<DecodedAudio>, Comparable<DecoderTask> {
		private final int priority;

		private DecoderTask(int priority) {
			this.priority = priority;
		}

		@Override
		public int compareTo(@NotNull DecoderTask task) {
			Objects.requireNonNull(task, "task is null");

			return 0;
		}

		@NotNull
		@Override
		public DecodedAudio call() throws Exception {
			return null;
		}
	}

	protected interface DecodedAudio {

	}

	private static class AudioDecoderThreadFactory implements ThreadFactory {
		private final AtomicInteger id;

		public AudioDecoderThreadFactory() {
			this.id = new AtomicInteger(0);
		}

		@NotNull
		@Override
		public Thread newThread(@NotNull Runnable r) {
			Objects.requireNonNull(r, "r is null");

			Thread thread = new Thread(r);
			thread.setDaemon(false);
			thread.setName("AudioDecoder-" + this.id.getAndIncrement());
			return thread;
		}
	}
}

/*
播放音频:
	1. 如果音频已加载
	1.1. 使用缓存并返回音频对象
	2. 返回音频对象的同时在后台加载
	3. 如果音频未加载完成则等待直到超时
	4. 否则播放音频
 */