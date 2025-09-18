package io.github.wsyong11.gameforge.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

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
