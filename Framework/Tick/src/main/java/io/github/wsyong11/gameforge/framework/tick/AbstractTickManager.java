package io.github.wsyong11.gameforge.framework.tick;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractTickManager implements TickManager {
	@NotNull
	protected abstract TickingTask buildBuilder(@NotNull DefaultTaskBuilder builder);

	@NotNull
	@Override
	public TaskBuilder buildTask(@NotNull Tickable tickable) {
		Objects.requireNonNull(tickable, "tickable is null");
		return new DefaultTaskBuilder(tickable, this);
	}

	protected static class DefaultTaskBuilder implements TaskBuilder {
		private final Tickable tickable;
		private final AbstractTickManager manager;

		private long initialDelayTick;
		private long frequencyTick;
		private int priority;

		public DefaultTaskBuilder(@NotNull Tickable tickable, @NotNull AbstractTickManager manager) {
			Objects.requireNonNull(tickable, "tickable is null");
			Objects.requireNonNull(manager, "manager is null");

			this.tickable = tickable;
			this.manager = manager;

			this.initialDelayTick = 0L;
			this.frequencyTick = 1L;
			this.priority = PRIORITY_NORMAL;
		}

		@NotNull
		@Override
		public DefaultTaskBuilder initialDelay(long delayTick) {
			this.initialDelayTick = delayTick;
			return this;
		}

		@Override
		public long getInitialDelay() {
			return this.initialDelayTick;
		}

		@NotNull
		@Override
		public DefaultTaskBuilder frequency(long frequencyTick) {
			this.frequencyTick = frequencyTick;
			return this;
		}

		@Override
		public long getFrequency() {
			return this.frequencyTick;
		}

		@NotNull
		@Override
		public DefaultTaskBuilder priority(int priority) {
			this.priority = priority;
			return this;
		}

		@Override
		public int getPriority() {
			return this.priority;
		}

		@NotNull
		public Tickable getTickable() {
			return this.tickable;
		}

		@NotNull
		@Override
		public TickingTask build() {
			return this.manager.buildBuilder(this);
		}
	}
}
