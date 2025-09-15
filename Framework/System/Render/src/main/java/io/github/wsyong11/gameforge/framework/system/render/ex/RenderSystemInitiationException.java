package io.github.wsyong11.gameforge.framework.system.render.ex;

import org.jetbrains.annotations.Nullable;

public class RenderSystemInitiationException extends Exception {
	public RenderSystemInitiationException(@Nullable String message) {
		super(message);
	}

	public RenderSystemInitiationException(@Nullable Throwable cause) {
		super(cause);
	}

	public RenderSystemInitiationException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
}
