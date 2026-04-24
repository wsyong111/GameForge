package io.github.wsyong11.gameforge.framework.tick;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.Nameable;
import io.github.wsyong11.gameforge.util.concurrent.DeferredValue;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;

public class SimpleTickManager extends AbstractTickManager {
	private static final Logger LOGGER = Log.getLogger();

	private final Queue<SimpleTickingTask> taskQueue;

	private volatile long currentTick;

	public SimpleTickManager() {
		this.taskQueue = new PriorityQueue<>(Comparator
			.comparingLong(SimpleTickingTask::getNextTick)
			.thenComparingInt(t -> -t.getCurrentPriority()));

		this.currentTick = 0L;
	}

	@Override
	public long getCurrentTick() {
		return this.currentTick;
	}

	@Override
	public void tick() {
		TickInfoImpl tickInfo = new TickInfoImpl(this.currentTick, 0); // TODO: 2025/11/8 Calculate delta time
		synchronized (this.taskQueue) {
			while (!this.taskQueue.isEmpty()) {
				SimpleTickingTask task = this.taskQueue.peek();

				if (task.getNextTick() > this.currentTick)
					break;

				this.taskQueue.poll();

				if (task.isCanceled() || task.isPaused())
					continue;

				task.tick(tickInfo);

				task.updateNextTick(this.currentTick + task.getCurrentFrequencyTick());
				if (!task.isCanceled() && !task.isTickOnce()) {
					task.updateValue();
					this.taskQueue.offer(task);
				}
			}

			this.currentTick++;
		}
	}

	protected void schedule(@NotNull SimpleTickingTask task) {
		Objects.requireNonNull(task, "task is null");

		LOGGER.debug("Schedule ticking task {}", task);

		synchronized (this.taskQueue) {
			this.taskQueue.add(task);
		}
	}

	@NotNull
	@Override
	public TickingTask schedule(@NotNull Tickable tickable, long startTick, long frequencyTick) {
		Objects.requireNonNull(tickable, "tickable is null");

		if (startTick < 0)
			throw new IllegalArgumentException("Start tick cannot be negative");

		SimpleTickingTask task = new SimpleTickingTask(tickable, this.currentTick + startTick, frequencyTick, PRIORITY_NORMAL);
		this.schedule(task);
		return task;
	}

	@NotNull
	@Override
	protected TickingTask buildBuilder(@NotNull DefaultTaskBuilder builder) {
		Objects.requireNonNull(builder, "builder is null");

		SimpleTickingTask task = new SimpleTickingTask(
			builder.getTickable(),
			this.currentTick + builder.getInitialDelay(),
			builder.getFrequency(),
			builder.getPriority()
		);
		this.schedule(task);
		return task;
	}

	protected static class SimpleTickingTask implements TickingTask, Tickable {
		private static final Logger LOGGER = Log.getLogger();

		private final Tickable task;

		private final DeferredValue<Integer> priority;
		private final DeferredValue<Long> frequencyTick;

		private volatile boolean canceled;
		private volatile boolean paused;

		private volatile long nextTick;

		protected SimpleTickingTask(@NotNull Tickable task, long startTick, long frequencyTick, int priority) {
			Objects.requireNonNull(task, "task is null");

			this.task = task;

			this.priority = DeferredValue.of(priority);
			this.frequencyTick = DeferredValue.of(frequencyTick);

			this.canceled = false;
			this.paused = false;

			this.nextTick = startTick;
		}

		@Override
		public void tick(@NotNull TickInfo info) {
			try {
				this.task.tick(info);
			} catch (Throwable e) {
				LOGGER.error("Uncaught exception occurred while running Tickable: {}", this.task, e);
			}
		}

		public void updateValue() {
			this.priority.update();
			this.frequencyTick.update();
		}

		@Override
		public void cancel() {
			this.canceled = true;
		}

		@Override
		public void pause() {
			if (this.canceled)
				return;
			this.paused = true;
		}

		@Override
		public void resume() {
			if (this.canceled)
				return;
			this.paused = false;
		}

		@Override
		public boolean isCanceled() {
			return this.canceled;
		}

		@Override
		public boolean isPaused() {
			return this.paused;
		}

		@Override
		public int getPriority() {
			return this.priority.getNewValue();
		}

		@Override
		public void setPriority(int priority) {
			this.priority.setNewValue(priority);
		}

		public int getCurrentPriority() {
			return this.priority.getValue();
		}

		@Override
		public long getFrequencyTick() {
			return this.frequencyTick.getNewValue();
		}

		@Override
		public void setFrequencyTick(long frequencyTick) {
			this.frequencyTick.setNewValue(frequencyTick);
		}

		public long getCurrentFrequencyTick() {
			return this.frequencyTick.getValue();
		}

		public boolean isTickOnce() {
			return this.getCurrentFrequencyTick() <= 0;
		}

		@Override
		public long getNextTick() {
			return this.nextTick;
		}

		public void updateNextTick(long tick) {
			this.nextTick = tick;
		}

		@NotNull
		@Override
		public Tickable getTickable() {
			return this.task;
		}

		@Override
		public String toString() {
			String priority = this.priority.toString();

			String frequency = this.isTickOnce()
				? this.frequencyTick + " (ONCE)"
				: this.frequencyTick.toString();

			String state;
			if (this.canceled)
				state = "CANCELED";
			else if (this.paused)
				state = "PAUSED";
			else
				state = "RUNNING";

			String taskName = Nameable.getName(this.task);

			return "SimpleTickingTask[priority=" + priority + ", " + "frequency=" + frequency + ", " + state + ", " + taskName + "]";
		}
	}
}
