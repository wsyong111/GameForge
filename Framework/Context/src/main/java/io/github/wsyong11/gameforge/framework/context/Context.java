package io.github.wsyong11.gameforge.framework.context;

import org.jetbrains.annotations.NotNull;

public abstract class Context {
	public abstract <T> T get(@NotNull Class<T> type);
}
