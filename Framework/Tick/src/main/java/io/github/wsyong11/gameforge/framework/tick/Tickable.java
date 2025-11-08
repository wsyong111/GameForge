package io.github.wsyong11.gameforge.framework.tick;

import org.jetbrains.annotations.NotNull;

public interface Tickable {
	void tick(@NotNull TickInfo info);
}
