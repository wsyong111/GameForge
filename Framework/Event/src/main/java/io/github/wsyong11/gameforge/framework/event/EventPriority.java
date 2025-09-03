package io.github.wsyong11.gameforge.framework.event;

import org.jetbrains.annotations.NotNull;

public enum EventPriority {
	HIGHEST(2),
	HIGH(1),
	NORMAL(0),
	LOW(-1),
	LOWEST(-2);

	private final int priority;

	EventPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return this.priority;
	}

	public static int compare(@NotNull EventPriority left, @NotNull EventPriority right) {
		return Integer.compare(left.getPriority(), right.getPriority());
	}
}

