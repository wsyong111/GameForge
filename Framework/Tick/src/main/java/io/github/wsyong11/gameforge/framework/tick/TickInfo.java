package io.github.wsyong11.gameforge.framework.tick;

public interface TickInfo {
	long getCurrentTick();

	double getDeltaTimeMs();
}
