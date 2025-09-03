package io.github.wsyong11.gameforge.game.common.tick;

import io.github.wsyong11.gameforge.framework.annotation.UnsafeAPI;
import org.jetbrains.annotations.NotNull;

public interface TickingTask {
	void cancel();

	boolean isCancelled();

	boolean isDone();

    long getNextTick();

    int getInterval();

	@NotNull
	@UnsafeAPI
	Tickable getTickable();
}
