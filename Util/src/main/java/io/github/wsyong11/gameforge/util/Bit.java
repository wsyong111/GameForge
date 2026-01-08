package io.github.wsyong11.gameforge.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

@UtilityClass
public class Bit {
	public static int add(int flags, int flag) {
		return flags | flag;
	}

	public static long add(long flags, long flag) {
		return flags | flag;
	}

	public static int remove(int flags, int flag) {
		return flags & ~flag;
	}

	public static long remove(long flags, long flag) {
		return flags & ~flag;
	}

	public static boolean has(int flags, int flag) {
		return (flags & flag) == flag;
	}

	public static boolean has(long flags, long flag) {
		return (flags & flag) == flag;
	}

	public static boolean isValid(int flags, int allFlags) {
		return (flags & ~allFlags) == 0;
	}

	public static boolean isValid(long flags, long allFlags) {
		return (flags & ~allFlags) == 0L;
	}

	@NotNull
	public static String toString(int flags) {
		return String.format("IF%32s", Integer.toBinaryString(flags)).replace(' ', '0');
	}

	@NotNull
	public static String toString(int flags, @NotNull Map<Integer, String> mapping) {
		Objects.requireNonNull(mapping, "mapping is null");

		StringBuilder sb = new StringBuilder();
		int knownMask = 0;

		for (var entry : mapping.entrySet()) {
			int bit = entry.getKey();
			if (has(flags, bit)) {
				if (!sb.isEmpty()) sb.append(" | ");
				sb.append(entry.getValue());
				knownMask |= bit;
			}
		}

		int unknown = flags & ~knownMask;
		if (unknown != 0) {
			if (!sb.isEmpty()) sb.append(" | ");
			sb.append("0b").append(Integer.toBinaryString(unknown));
		}

		return sb.isEmpty() ? "[NONE]" : sb.toString();
	}

	@NotNull
	public static String toString(long flags) {
		return String.format("LF%64s", Long.toBinaryString(flags)).replace(' ', '0');
	}

	@NotNull
	public static String toString(long flags, @NotNull Map<Long, String> mapping) {
		Objects.requireNonNull(mapping, "mapping is null");

		StringBuilder sb = new StringBuilder();
		long knownMask = 0;

		for (var entry : mapping.entrySet()) {
			long bit = entry.getKey();
			if (has(flags, bit)) {
				if (!sb.isEmpty()) sb.append(" | ");
				sb.append(entry.getValue());
				knownMask |= bit;
			}
		}

		long unknown = flags & ~knownMask;
		if (unknown != 0L) {
			if (!sb.isEmpty()) sb.append(" | ");
			sb.append("0b").append(Long.toBinaryString(unknown));
		}

		return sb.isEmpty() ? "[NONE]" : sb.toString();
	}
}
