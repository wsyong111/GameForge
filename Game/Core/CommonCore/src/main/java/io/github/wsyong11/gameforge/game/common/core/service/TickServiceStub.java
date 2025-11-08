package io.github.wsyong11.gameforge.game.common.core.service;

import io.github.wsyong11.gameforge.framework.tick.TickManager;
import io.github.wsyong11.gameforge.framework.tick.Tickable;
import io.github.wsyong11.gameforge.framework.tick.TickingTask;
import io.github.wsyong11.gameforge.game.common.service.TickService;
import io.github.wsyong11.gameforge.util.Wrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TickServiceStub extends Wrapper<TickManager> implements TickService {
	public TickServiceStub(@NotNull TickManager manager) {
		super(Objects.requireNonNull(manager, "manager is null"));
	}

	@NotNull
	@Override
	public TickingTask schedule(@NotNull Tickable tickable, long initialDelayTick, long frequencyTick) {
		return this.delegate().schedule(tickable, initialDelayTick, frequencyTick);
	}

	@NotNull
	@Override
	public TaskBuilder buildTask(@NotNull Tickable tickable) {
		return this.delegate().buildTask(tickable);
	}

	@Override
	public void tick() {
		this.delegate().tick();
	}

	@Override
	public long getCurrentTick() {
		return this.delegate().getCurrentTick();
	}

	@NotNull
	@Override
	public TickingTask schedule(@NotNull Tickable tickable, long frequencyTick) {
		return this.delegate().schedule(tickable, frequencyTick);
	}

	@NotNull
	@Override
	public TickingTask schedule(@NotNull Tickable tickable) {
		return this.delegate().schedule(tickable);
	}

	@NotNull
	@Override
	public TickingTask scheduleOnce(@NotNull Tickable tickable) {
		return this.delegate().scheduleOnce(tickable);
	}

	@NotNull
	@Override
	public TickingTask scheduleOnce(@NotNull Tickable tickable, long startTick) {
		return this.delegate().scheduleOnce(tickable, startTick);
	}

	@Nullable
	@Override
	public TickManager getDelegate() {
		return super.getDelegate();
	}
}
