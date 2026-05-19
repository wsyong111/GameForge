package io.github.wsyong11.gameforge.framework.system.graphic.surface.window.ex;

import org.jetbrains.annotations.Nullable;

public class WindowSurfaceBackedInitException extends WindowSurfaceException {
	public WindowSurfaceBackedInitException() {
	}

	public WindowSurfaceBackedInitException(@Nullable String message) {
		super(message);
	}

	public WindowSurfaceBackedInitException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public WindowSurfaceBackedInitException(@Nullable Throwable cause) {
		super(cause);
	}
}
