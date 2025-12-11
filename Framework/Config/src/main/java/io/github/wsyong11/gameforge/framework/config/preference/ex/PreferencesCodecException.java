package io.github.wsyong11.gameforge.framework.config.preference.ex;

import org.jetbrains.annotations.NotNull;

public class PreferencesCodecException extends RuntimeException {
	public PreferencesCodecException(@NotNull String message) {
		super(message);
	}

	public PreferencesCodecException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
}
