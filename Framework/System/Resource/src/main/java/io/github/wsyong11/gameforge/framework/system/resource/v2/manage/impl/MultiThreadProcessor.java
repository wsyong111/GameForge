package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl;

import io.github.wsyong11.gameforge.util.exception.ExceptionFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class MultiThreadProcessor {
	private final Semaphore semaphore;

	private final ExecutorService executor;

	public MultiThreadProcessor(
		int maxThreadCount,
		long keepAliveTime,
		@NotNull TimeUnit unit,
		@NotNull ThreadFactory threadFactory
	) {
		Objects.requireNonNull(unit, "unit is null");
		Objects.requireNonNull(threadFactory, "threadFactory is null");

		if (maxThreadCount <= 0)
			throw new IllegalArgumentException("Max thread count cannot be zero or negative");

		this.semaphore = new Semaphore(maxThreadCount);

		this.executor = new ThreadPoolExecutor(
			1,
			maxThreadCount,
			keepAliveTime,
			unit,
			new SynchronousQueue<>(),
			threadFactory,
			new ThreadPoolExecutor.AbortPolicy()
		);
	}

	@NotNull
	@Unmodifiable
	public <I, O, R> R process(
		@NotNull Collection<I> input,
		long pollingTimeout,
		@NotNull TimeUnit unit,
		@NotNull Supplier<R> outputSupplier,
		@NotNull BiFunction<R, O, R> combiner,
		@NotNull ExceptionFunction<I, O, Exception> processor
	) throws InterruptedException, ExecutionException {
		Objects.requireNonNull(input, "input is null");
		Objects.requireNonNull(unit, "unit is null");
		Objects.requireNonNull(outputSupplier, "outputSupplier is null");
		Objects.requireNonNull(combiner, "combiner is null");
		Objects.requireNonNull(processor, "processor is null");

		Deque<I> tasks = new ArrayDeque<>(input);

		Deque<Future<O>> futures = new ArrayDeque<>();

		R result = outputSupplier.get();

		try {
			while (!tasks.isEmpty() || !futures.isEmpty()) {
				I taskValue = tasks.poll();
				if (taskValue != null && this.semaphore.tryAcquire(pollingTimeout, unit)) {
					try {
						Future<O> future = this.executor.submit(() -> this.executeTask(taskValue, processor));
						futures.add(future);
						continue;
					} catch (RejectedExecutionException ignored) {
						tasks.add(taskValue);
						this.semaphore.release();
					}
				}

				Future<O> future = futures.poll();
				if (future != null) {
					O taskResult;
					if (!tasks.isEmpty()) {
						try {
							taskResult = future.get(pollingTimeout, unit);
						} catch (TimeoutException ignored) {
							futures.add(future);
							continue;
						}
					} else {
						taskResult = future.get();
					}

					result = combiner.apply(result, taskResult);
				}
			}
		} catch (Throwable e) {
			for (Future<O> future : futures)
				future.cancel(true);

			throw e;
		}

		return result;
	}

	@Nullable
	protected <I, O> O executeTask(@Nullable I value, @NotNull ExceptionFunction<I, O, Exception> processor) throws Exception {
		Objects.requireNonNull(processor, "processor is null");

		try {
			return processor.apply(value);
		} finally {
			this.semaphore.release();
		}
	}

	public void shutdown(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
		Objects.requireNonNull(unit, "unit is null");

		this.executor.shutdown();
		if (!this.executor.awaitTermination(timeout, unit))
			this.executor.shutdownNow();
	}

	class BlockingPolicy implements RejectedExecutionHandler {
		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			try {
				// 阻塞直到队列有空间
				executor.getQueue().put(r);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RejectedExecutionException("Interrupted while waiting", e);
			}
		}
	}
}
//但是如果用CallerRunsPolicy在被在主线程运行的任务运行太长导致不提交任务要怎么处理，用LinkedBlockingQueue(capacity) + AbortPolicy则只会抛出RejectedExecutionException不会阻塞，CompletionService也只会一股脑吧任务往ServiceExecutor丢