package io.github.wsyong11.gameforge.util.number;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

@UtilityClass
public class Bit {
	public static int flag(int index) {
		return 1 << index;
	}

	public static long flagL(int index) {
		return 1L << index;
	}

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

	public static boolean hasAny(int flags, int mask) {
		return (flags & mask) != 0;
	}

	public static boolean hasAny(long flags, long mask) {
		return (flags & mask) != 0L;
	}

	public static boolean isValid(int flags, int allFlags) {
		return (flags & ~allFlags) == 0;
	}

	public static boolean isValid(long flags, long allFlags) {
		return (flags & ~allFlags) == 0L;
	}

	public static int invert(int flags, int allFlags) {
		return (~flags) & allFlags;
	}

	public static long invert(long flags, long allFlags) {
		return (~flags) & allFlags;
	}

	public static int toggle(int flags, int flag) {
		return flags ^ flag;
	}

	public static long toggle(long flags, long flag) {
		return flags ^ flag;
	}

	public static int set(int flags, int flag) {
		return flags | flag;
	}

	public static long set(long flags, long flag) {
		return flags | flag;
	}

	public static int unset(int flags, int flag) {
		return flags & ~flag;
	}

	public static long unset(long flags, long flag) {
		return flags & ~flag;
	}

	public static int extract(int flags, int mask) {
		return flags & mask;
	}

	public static long extract(long flags, long mask) {
		return flags & mask;
	}

	public static int count(int flags) {
		return Integer.bitCount(flags);
	}

	public static int count(long flags) {
		return Long.bitCount(flags);
	}

	public static int lowestBit(int flags) {
		return flags & -flags;
	}

	public static long lowestBit(long flags) {
		return flags & -flags;
	}

	public static int highestBit(int flags) {
		return Integer.highestOneBit(flags);
	}

	public static long highestBit(long flags) {
		return Long.highestOneBit(flags);
	}

	public static int clearLowestBit(int flags) {
		return flags & (flags - 1);
	}

	public static long clearLowestBit(long flags) {
		return flags & (flags - 1);
	}

	public static boolean isPowerOfTwo(int flags) {
		return flags > 0 && (flags & (flags - 1)) == 0;
	}

	public static boolean isPowerOfTwo(long flags) {
		return flags > 0L && (flags & (flags - 1)) == 0L;
	}

	public static void forEachBit(int flags, @NotNull IntConsumer action) {
		Objects.requireNonNull(action, "action is null");
		while (flags != 0) {
			int bit = lowestBit(flags);
			action.accept(bit);
			flags = clearLowestBit(flags);
		}
	}

	public static void forEachBit(long flags, @NotNull LongConsumer action) {
		Objects.requireNonNull(action, "action is null");
		while (flags != 0L) {
			long bit = lowestBit(flags);
			action.accept(bit);
			flags = clearLowestBit(flags);
		}
	}

	public static int @NotNull [] toBits(int flags) {
		int[] result = new int[count(flags)];
		int i = 0;

		while (flags != 0) {
			int bit = lowestBit(flags);
			result[i++] = bit;
			flags = clearLowestBit(flags);
		}
		return result;
	}

	public static long @NotNull [] toBits(long flags) {
		long[] result = new long[count(flags)];
		int i = 0;

		while (flags != 0) {
			long bit = lowestBit(flags);
			result[i++] = bit;
			flags = clearLowestBit(flags);
		}
		return result;
	}

	public static int merge(int... flags) {
		Objects.requireNonNull(flags, "flags is null");

		int result = 0;
		for (int f : flags)
			result |= f;
		return result;
	}

	public static long merge(long... flags) {
		Objects.requireNonNull(flags, "flags is null");

		long result = 0;
		for (long f : flags)
			result |= f;
		return result;
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
