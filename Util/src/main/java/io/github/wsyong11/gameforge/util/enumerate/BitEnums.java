package io.github.wsyong11.gameforge.util.enumerate;

import io.github.wsyong11.gameforge.util.number.Bit;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@UtilityClass
public class BitEnums {
	public static <T extends Enum<T> & BitEnum> int toBit(@NotNull Iterable<T> enums) {
		Objects.requireNonNull(enums, "enums is null");

		int flags = 0;
		for (T e : enums)
			flags |= e.bit();
		return flags;
	}

	@NotNull
	@Unmodifiable
	public static <T extends Enum<T> & BitEnum> Set<T> fromBit(@NotNull Class<T> type, int flags) {
		Objects.requireNonNull(type, "type is null");

		Set<T> result = EnumSet.noneOf(type);
		for (T e : type.getEnumConstants()) {
			if (Bit.has(flags, e.bit()))
				result.add(e);
		}

		return Collections.unmodifiableSet(result);
	}

	@Nullable
	public static <T extends Enum<T> & BitEnum> T fromBitFirst(@NotNull Class<T> type, int flags) {
		Objects.requireNonNull(type, "type is null");

		for (T e : type.getEnumConstants()) {
			if (Bit.has(flags, e.bit()))
				return e;
		}

		return null;
	}

	public static <T extends Enum<T> & BitEnum> void validate(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		int seen = 0;
		for (T e : type.getEnumConstants()) {
			int bit = e.bit();
			if (Bit.hasAny(seen, bit))
				throw new IllegalStateException("Duplicate bit: " + e);
			seen |= bit;
		}
	}

	@NotNull
	public static <T extends Enum<T> & BitEnum> String toString(Class<T> type, int flags) {
		return fromBit(type, flags).toString();
	}
}
