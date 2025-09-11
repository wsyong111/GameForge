package io.github.wsyong11.gameforge.framework.system.window;

import io.github.wsyong11.gameforge.framework.annotation.UnsafeAPI;
import org.jetbrains.annotations.NotNull;

public interface WindowManager {
	@NotNull
	Window createWindow(@NotNull WindowConfig config);

	@UnsafeAPI
	long getNativeHandle(@NotNull Window window);
}
