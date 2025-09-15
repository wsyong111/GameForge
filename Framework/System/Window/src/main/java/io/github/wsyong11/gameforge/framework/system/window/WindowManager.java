package io.github.wsyong11.gameforge.framework.system.window;

import io.github.wsyong11.gameforge.framework.annotation.ThreadSensitive;
import io.github.wsyong11.gameforge.framework.annotation.UnsafeAPI;
import io.github.wsyong11.gameforge.framework.system.window.ex.WindowException;
import org.jetbrains.annotations.NotNull;

public interface WindowManager extends AutoCloseable {
	void init() throws WindowException;

	@NotNull
	@ThreadSensitive
	Window createWindow(@NotNull WindowConfig config) throws WindowException;

	void update();

	@UnsafeAPI
	long getNativeHandle(@NotNull Window window);

	@Override
	void close();
}
