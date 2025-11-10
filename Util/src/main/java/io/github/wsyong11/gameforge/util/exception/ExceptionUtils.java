package io.github.wsyong11.gameforge.util.exception;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@UtilityClass
public class ExceptionUtils {
	@NotNull
	public static String toOneLineString(@NotNull Throwable e) {
		Objects.requireNonNull(e, "e is null");

		Throwable cause = e.getCause();
		return cause != null
			? e + " cause by " + cause
			: e.toString();
	}
}
