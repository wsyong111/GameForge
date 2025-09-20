package io.github.wsyong11.gameforge.game.common.core.tick;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.game.common.tick.TickManager;
import io.github.wsyong11.gameforge.game.common.tick.Tickable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class RootTickManager implements TickManager {
	private static final Logger LOGGER = Log.getLogger();

	private final List<TickTask> taskList;

	private long currentTick;

	public RootTickManager() {
		this.taskList = new ArrayList<>();

		this.currentTick = 0L;
	}

	private void addTask(int priority, @NotNull Tickable tickable, long startTick, long frequencyTick, int runCount) {
		synchronized (this.taskList) {
			this.taskList.add(new TickTask(priority, tickable, this.currentTick + startTick, frequencyTick, runCount));
			this.taskList.sort(TickTask::compareTo);
		}
	}

	@Override
	public void register(int priority, @NotNull Tickable tickable) {
		Objects.requireNonNull(tickable, "tickable is null");
		this.addTask(priority, tickable, 0, 1L, Integer.MAX_VALUE);
	}

	@Override
	public void unregister(@NotNull Tickable tickable) {
		Objects.requireNonNull(tickable, "tickable is null");

		synchronized (this.taskList) {
			Iterator<TickTask> iterator = this.taskList.iterator();

			while (iterator.hasNext()) {
				TickTask item = iterator.next();
				if (!item.is(tickable))
					continue;

				iterator.remove();
				break;
			}
		}
	}

	@Override
	public boolean isRegistered(@NotNull Tickable tickable) {
		Objects.requireNonNull(tickable, "tickable is null");

		return this.taskList
			.stream()
			.anyMatch(item -> item.is(tickable));
	}

	@Override
	public void schedule(int priority, @NotNull Tickable tickable, long frequencyTick) {
		Objects.requireNonNull(tickable, "tickable is null");
		this.addTask(priority, tickable, 0, frequencyTick, Integer.MAX_VALUE);
	}

	@Override
	public void runOnce(int priority, @NotNull Tickable tickable) {
		Objects.requireNonNull(tickable, "tickable is null");
		this.addTask(priority, tickable, 0, 1L, 1);
	}

	@Override
	public long getCurrentTick() {
		return this.currentTick;
	}

	@Override
	public void tick() {
		synchronized (this.taskList) {
			Iterator<TickTask> iterator = this.taskList.iterator();

			while (iterator.hasNext()) {
				TickTask task = iterator.next();

				if (task.needRemove()) {
					iterator.remove();
					continue;
				}

				if (!task.needRun(this.currentTick))
					continue;

				task.tick();
			}

			this.currentTick++;
		}
	}

	private static class TickTask implements Comparable<TickTask> {
		private final int priority;
		private final Tickable tickable;

		private final long frequencyTick;

		private long nextRunTick;
		private int remainingRunCount;

		private TickTask(
			int priority,
			@NotNull Tickable tickable,
			long startTick,
			long frequencyTick,
			int remainingRunCount
		) {
			Objects.requireNonNull(tickable, "tickable is null");

			this.priority = priority;
			this.tickable = tickable;

			this.frequencyTick = frequencyTick;

			this.nextRunTick = startTick;
			this.remainingRunCount = remainingRunCount;
		}

		public boolean is(@NotNull Tickable tickable) {
			return Objects.equals(this.tickable, tickable);
		}

		public boolean needRemove() {
			return this.remainingRunCount <= 0;
		}

		public boolean needRun(long currentTick) {
			return currentTick >= this.nextRunTick;
		}

		public void tick() {
			try {
				this.tickable.tick();
			} catch (Exception e) {
				LOGGER.error("Uncaught exception occurred while ticking {}", this.tickable, e);
			}

			if (this.remainingRunCount != Integer.MAX_VALUE && this.remainingRunCount > 0)
				this.remainingRunCount--;

			this.nextRunTick += this.frequencyTick;
		}

		@Override
		public int compareTo(@NotNull RootTickManager.TickTask o) {
			Objects.requireNonNull(o, "o is null");
			return Integer.compare(o.priority, this.priority);
		}
	}
}
