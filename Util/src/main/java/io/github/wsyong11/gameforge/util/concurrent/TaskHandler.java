package io.github.wsyong11.gameforge.util.concurrent;

import io.github.wsyong11.gameforge.util.FunctionUtils;
import io.github.wsyong11.gameforge.util.concurrent.signal.ThreadSignal;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class TaskHandler {
	private final Executor executor;
	private final ThreadMark owner;

	public TaskHandler(@NotNull Executor executor, @NotNull ThreadMark owner) {
		Objects.requireNonNull(executor, "executor is null");
		Objects.requireNonNull(owner, "owner is null");

		this.executor = executor;
		this.owner = owner;
	}

	public TaskHandler(@NotNull Executor executor) {
		Objects.requireNonNull(executor, "executor is null");
		this.executor = executor;
		this.owner = ThreadMark.get();
	}

	public void run(@NotNull Runnable task) {
		Objects.requireNonNull(task, "task is null");

		if (this.owner.check()) {
			task.run();
			return;
		}
		this.executor.execute(new Task<>(task));
	}

	public void runBlocking(@NotNull Runnable task) throws ExecutionException, InterruptedException {
		Objects.requireNonNull(task, "task is null");

		if (this.owner.check()) {
			task.run();
			return;
		}

		Task<Void> future = new Task<>(task);
		this.executor.execute(future);
		future.get();
	}

	public <T> T runBlocking(@NotNull Callable<T> task) throws ExecutionException, InterruptedException {
		Objects.requireNonNull(task, "task is null");

		if (this.owner.check()) {
			try {
				return task.call();
			} catch (Exception e) {
				throw new ExecutionException(e);
			}
		}

		Task<T> future = new Task<>(task);
		this.executor.execute(future);
		return future.get();
	}

	public <T> T runBlocking(@NotNull Callable<T> task, long timeoutMs, @NotNull TimeUnit unit) throws ExecutionException, InterruptedException {
		Objects.requireNonNull(task, "task is null");

		if (this.owner.check()) {
			try {
				return task.call();
			} catch (Exception e) {
				throw new ExecutionException(e);
			}
		}

		Task<T> future = new Task<>(task);
		this.executor.execute(future);
		return future.get(timeoutMs, unit);
	}

	public static class Task<T> implements Runnable {
		private final Callable<T> task;
		private final ThreadSignal signal;
		private volatile Throwable exception;
		private volatile T value;

		public Task(@NotNull Runnable task) {
			this(FunctionUtils.toCallable(task));
		}

		public Task(@NotNull Callable<T> task) {
			Objects.requireNonNull(task, "task is null");

			this.task = task;

			this.signal = new ThreadSignal();
			this.exception = null;
			this.value = null;
		}

		@Override
		public void run() {
			try {
				this.value = this.task.call();
			} catch (Throwable e) {
				this.exception = e;
			}
			this.signal.set();
		}

		@NotNull
		protected T getValue() throws ExecutionException {
			if (this.exception != null)
				throw new ExecutionException(this.exception);
			return this.value;
		}

		public T get() throws ExecutionException, InterruptedException {
			this.signal.await();
			return this.getValue();
		}

		public T get(long timeout, @NotNull TimeUnit unit) throws ExecutionException, InterruptedException {
			Objects.requireNonNull(unit, "unit is null");
			this.signal.await(timeout, unit);
			return this.getValue();
		}
	}
}
