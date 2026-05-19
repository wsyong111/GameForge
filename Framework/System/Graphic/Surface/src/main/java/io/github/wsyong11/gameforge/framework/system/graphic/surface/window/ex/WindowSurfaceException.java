package io.github.wsyong11.gameforge.framework.system.graphic.surface.window.ex;

import io.github.wsyong11.gameforge.framework.system.graphic.surface.ex.SurfaceException;
import org.jetbrains.annotations.Nullable;

public class WindowSurfaceException extends SurfaceException {
	public WindowSurfaceException() {
	}

	public WindowSurfaceException(@Nullable String message) {
		super(message);
	}

	public WindowSurfaceException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public WindowSurfaceException(@Nullable Throwable cause) {
		super(cause);
	}
}
