package io.github.wsyong11.gameforge.util.exception;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

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

	@Contract("null, _, _ -> null; !null, _, _ -> !null;")
	@Nullable
	public static <T extends Throwable, E extends Throwable> T wrap(@Nullable E e, @NotNull Class<T> type, @NotNull Function<E, T> convertor) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(convertor, "convertor is null");

		if (e == null)
			return null;

		if (type.isInstance(e))
			return type.cast(e);

		return convertor.apply(e);
	}
}
