package io.github.wsyong11.gameforge.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

@UtilityClass
public class FunctionUtils {
	public static <T> T run(@NotNull Supplier<T> supplier){
		Objects.requireNonNull(supplier, "supplier is null");
		return supplier.get();
	}
}
