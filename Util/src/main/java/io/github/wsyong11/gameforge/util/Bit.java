package io.github.wsyong11.gameforge.util;

import lombok.experimental.UtilityClass;

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
}
