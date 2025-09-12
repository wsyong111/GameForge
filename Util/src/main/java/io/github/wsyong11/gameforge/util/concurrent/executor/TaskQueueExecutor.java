package io.github.wsyong11.gameforge.util.concurrent.executor;

import org.jetbrains.annotations.NotNull;

import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * 基于双队列的任务队列执行器，每次运行都会交换任务队列，在运行任务期间新加入的任务不会被运行，将被安排到下一次运行周期
 */
public class TaskQueueExecutor implements Executor {
	private volatile Deque<Runnable> currentQueue;
	private volatile Deque<Runnable> pendingQueue;

	/**
	 * 实例化任务队列执行器
	 */
	public TaskQueueExecutor() {
		this.currentQueue = new ConcurrentLinkedDeque<>();
		this.pendingQueue = new ConcurrentLinkedDeque<>();
	}

	private void swap() {
		synchronized (this) {
			Deque<Runnable> temp = this.currentQueue;
			this.currentQueue = this.pendingQueue;
			this.pendingQueue = temp;
		}
	}

	/**
	 * 交换任务队列并运行队列中的所有任务
	 *
	 * @param exceptionHandler 异常处理器回调
	 * @return 已运行的任务数量，包括抛出异常的任务
	 */
	public int run(@NotNull Consumer<Throwable> exceptionHandler) {
		Objects.requireNonNull(exceptionHandler, "exceptionHandler is null");

		this.swap();

		int count = 0;
		while (true) {
			Runnable runnable = this.currentQueue.pollFirst();
			if (runnable == null)
				break;

			try {
				runnable.run();
			} catch (Throwable e) {
				exceptionHandler.accept(e);
			}

			count++;
		}
		return count;
	}

	/**
	 * 清空所有任务队列
	 */
	public void clear() {
		this.pendingQueue.clear();
		this.currentQueue.clear();
	}

	/**
	 * 检查当前任务队列是否为空
	 *
	 * @return 当前任务队列是否为空
	 */
	public boolean isEmpty() {
		return this.pendingQueue.isEmpty();
	}

	@Override
	public void execute(@NotNull Runnable command) {
		Objects.requireNonNull(command, "command is null");
		this.pendingQueue.addLast(command);
	}
}
