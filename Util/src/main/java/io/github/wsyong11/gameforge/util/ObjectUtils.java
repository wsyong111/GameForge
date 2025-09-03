package io.github.wsyong11.gameforge.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class ObjectUtils {
	@NotNull
	public static String safeToString(@Nullable Object o) {
		try {
			return String.valueOf(o);
		} catch (Throwable e) {
			try {
				return "<Error: " + e.getClass().getName() + ": " + e.getMessage() + ">";
			} catch (Throwable ignored) {
				return "<Error in toString()>";
			}
		}
	}
}
