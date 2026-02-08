package io.github.wsyong11.gameforge.framework.context;

import org.jetbrains.annotations.NotNull;

public interface ContextScope extends AutoCloseable{
	@NotNull
	Context getContext();

	@Override
	void close();
}
