package io.github.wsyong11.gameforge.util.number;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Maths {
	public static int lerp(int a, int b, float t) {
		return (int) (a + (b - a) * t);
	}

	public static long lerp(long a, long b, float t) {
		return (long) (a + (b - a) * t);
	}

	public static float lerp(float a, float b, float t) {
		return a + (b - a) * t;
	}

	public static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}

	public static int clamp(int min, int value, int max) {
		return Math.max(min, Math.min(value, max));
	}

	public static long clamp(long min, long value, long max) {
		return Math.max(min, Math.min(value, max));
	}

	public static float clamp(float min, float value, float max) {
		return Math.max(min, Math.min(value, max));
	}

	public static double clamp(double min, double value, double max) {
		return Math.max(min, Math.min(value, max));
	}
}
