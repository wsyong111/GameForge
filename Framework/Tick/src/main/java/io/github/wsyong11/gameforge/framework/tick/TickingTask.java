package io.github.wsyong11.gameforge.framework.tick;

import io.github.wsyong11.gameforge.framework.annotation.UnsafeAPI;
import org.jetbrains.annotations.NotNull;

public interface TickingTask {
	void cancel();

	void pause();

	void resume();

	boolean isCanceled();

	boolean isPaused();

	int getPriority();

	void setPriority(int priority);

	long getFrequencyTick();

	void setFrequencyTick(long frequencyTick);

	long getNextTick();

	@UnsafeAPI
	@NotNull
	Tickable getTickable();
}
