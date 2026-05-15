package io.github.wsyong11.gameforge.framework.system.graphic.surface.ex;

import org.jetbrains.annotations.Nullable;

public class SurfaceException extends RuntimeException {
	public SurfaceException() {
	}

	public SurfaceException(@Nullable String message) {
		super(message);
	}

	public SurfaceException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public SurfaceException(@Nullable Throwable cause) {
		super(cause);
	}
}
