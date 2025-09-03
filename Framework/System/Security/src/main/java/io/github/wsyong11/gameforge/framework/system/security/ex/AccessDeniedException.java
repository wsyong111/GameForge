package io.github.wsyong11.gameforge.framework.system.security.ex;

import org.jetbrains.annotations.Nullable;

public class AccessDeniedException extends SecurityException {
	public AccessDeniedException(@Nullable String s) {
		super(s);
	}

	public AccessDeniedException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public AccessDeniedException(@Nullable Throwable cause) {
		super(cause);
	}
}
