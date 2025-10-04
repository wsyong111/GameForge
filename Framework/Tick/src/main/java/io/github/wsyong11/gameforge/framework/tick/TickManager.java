package io.github.wsyong11.gameforge.framework.tick;

import org.jetbrains.annotations.NotNull;

public interface TickManager extends TickSourceProvider {
	int PRIORITY_VERY_LOW = -100;
	int PRIORITY_LOW = -50;
	int PRIORITY_NORMAL = 0;
	int PRIORITY_HIGH = 50;
	int PRIORITY_VERY_HIGH = 100;

	@NotNull
	TickingTask schedule(@NotNull Tickable tickable, long initialDelayTick, long frequencyTick);

	@NotNull
	default TickingTask schedule(@NotNull Tickable tickable, long frequencyTick) {
		return this.schedule(tickable, 0, frequencyTick);
	}

	@NotNull
	default TickingTask schedule(@NotNull Tickable tickable) {
		return this.schedule(tickable, 0, 1);
	}

	@NotNull
	default TickingTask scheduleOnce(@NotNull Tickable tickable) {
		return this.schedule(tickable, 0, 0);
	}

	@NotNull
	default TickingTask scheduleOnce(@NotNull Tickable tickable, long startTick) {
		return this.schedule(tickable, startTick, 0);
	}

	@NotNull
	TaskBuilder buildTask(@NotNull Tickable tickable);

	void tick();

	interface TaskBuilder {
		@NotNull
		TaskBuilder initialDelay(long delayTick);

		long getInitialDelay();

		@NotNull
		TaskBuilder frequency(long frequencyTick);

		long getFrequency();

		@NotNull
		TaskBuilder priority(int priority);

		int getPriority();

		@NotNull
		TickingTask build();
	}
}
