package io.github.wsyong11.gameforge.game.common.tick;

import org.jetbrains.annotations.NotNull;

public interface TickManager extends Tickable {
	int PRIORITY_VERY_LOW = -100;
	int PRIORITY_LOW = -50;
	int PRIORITY_NORMAL = 0;
	int PRIORITY_HIGH = 50;
	int PRIORITY_VERY_HIGH = 100;

	void register(int priority, @NotNull Tickable tickable);

	default void register(@NotNull Tickable tickable) {
		this.register(PRIORITY_NORMAL, tickable);
	}

	void unregister(@NotNull Tickable tickable);

	boolean isRegistered(@NotNull Tickable tickable);

	// -------------------------------------------------------------------------------------------------------------- //

	void schedule(int priority, @NotNull Tickable tickable, long frequencyTick);

	default void schedule(@NotNull Tickable tickable, long frequencyTick) {
		this.schedule(PRIORITY_NORMAL, tickable, frequencyTick);
	}

	void runOnce(int priority, @NotNull Tickable tickable);

	default void runOnce(@NotNull Tickable tickable) {
		this.runOnce(PRIORITY_NORMAL, tickable);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	long getCurrentTick();
}
