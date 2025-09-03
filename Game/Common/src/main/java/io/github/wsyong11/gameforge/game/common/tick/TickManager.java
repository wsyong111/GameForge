package io.github.wsyong11.gameforge.game.common.tick;

import org.jetbrains.annotations.NotNull;

public interface TickManager extends Tickable {
	void register(@NotNull Tickable tickable);

	void unregister(@NotNull Tickable tickable);

	boolean isRegistered(@NotNull Tickable tickable);

	long getCurrentTick();

	void schedule(@NotNull Tickable tickable, int tick);

	void runOnce(@NotNull Tickable tickable);
}
