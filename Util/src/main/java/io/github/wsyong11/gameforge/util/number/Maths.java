package io.github.wsyong11.gameforge.util.number;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Maths {
	public static final float EPSILON_FLOAT = 1e-6F;
	public static final double EPSILON_DOUBLE = 1e-6D;

	public static boolean equals(float a, float b) {
		return Math.abs(a - b) <= EPSILON_FLOAT;
	}

	public static boolean equals(double a, double b) {
		return Math.abs(a - b) <= EPSILON_DOUBLE;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public static int max(int a, int b) {
		return Math.max(a, b);
	}

	public static int max(int a, int b, int c) {
		return Math.max(Math.max(a, b), c);
	}

	public static long max(long a, long b) {
		return Math.max(a, b);
	}

	public static long max(long a, long b, long c) {
		return Math.max(Math.max(a, b), c);
	}

	public static float max(float a, float b) {
		return Math.max(a, b);
	}

	public static float max(float a, float b, float c) {
		return Math.max(Math.max(a, b), c);
	}

	public static double max(double a, double b) {
		return Math.max(a, b);
	}

	public static double max(double a, double b, double c) {
		return Math.max(Math.max(a, b), c);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public static int min(int a, int b) {
		return Math.min(a, b);
	}

	public static int min(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	public static long min(long a, long b) {
		return Math.min(a, b);
	}

	public static long min(long a, long b, long c) {
		return Math.min(Math.min(a, b), c);
	}

	public static float min(float a, float b) {
		return Math.min(a, b);
	}

	public static float min(float a, float b, float c) {
		return Math.min(Math.min(a, b), c);
	}

	public static double min(double a, double b) {
		return Math.min(a, b);
	}

	public static double min(double a, double b, double c) {
		return Math.min(Math.min(a, b), c);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public static int abs(int a) {
		return Math.abs(a);
	}

	public static long abs(long a) {
		return Math.abs(a);
	}

	public static float abs(float a) {
		return Math.abs(a);
	}

	public static double abs(double a) {
		return Math.abs(a);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public static int sign(int a) {
		return Integer.compare(a, 0);
	}

	public static int sign(long a) {
		return Long.compare(a, 0L);
	}

	public static int sign(float a) {
		return Float.compare(a, 0.0F);
	}

	public static int sign(double a) {
		return Double.compare(a, 0.0D);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public static boolean isPowerOfTwo(int x) {
		return x > 0 && (x & (x - 1)) == 0;
	}

	public static int nextPowerOfTwo(int x) {
		int n = 1;
		while (n < x) n <<= 1;
		return n;
	}

	// -------------------------------------------------------------------------------------------------------------- //
	// Distance xy

	public static float distance2(float dx, float dy) {
		return dx * dx + dy * dy;
	}

	public static float distance(float dx, float dy) {
		return (float) Math.sqrt(distance2(dx, dy));
	}

	public static float distance2Point(float x1, float y1, float x2, float y2) {
		return distance2(x2 - x1, y2 - y1);
	}

	public static float distancePoint(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt(distance2Point(x1, y1, x2, y2));
	}

	public static double distance2(double dx, double dy) {
		return dx * dx + dy * dy;
	}

	public static double distance(double dx, double dy) {
		return Math.sqrt(distance2(dx, dy));
	}

	public static double distance2Point(double x1, double y1, double x2, double y2) {
		return distance2(x2 - x1, y2 - y1);
	}

	public static double distancePoint(double x1, double y1, double x2, double y2) {
		return Math.sqrt(distance2Point(x1, y1, x2, y2));
	}

	// -------------------------------------------------------------------------------------------------------------- //
	// Distance xyz

	public static float distance2(float dx, float dy, float dz) {
		return dx * dx + dy * dy + dz * dz;
	}

	public static float distance(float dx, float dy, float dz) {
		return (float) Math.sqrt(distance2(dx, dy, dz));
	}

	public static float distance2Point(float x1, float y1, float z1, float x2, float y2, float z2) {
		return distance2(x2 - x1, y2 - y1, z2 - z1);
	}

	public static float distancePoint(float x1, float y1, float z1, float x2, float y2, float z2) {
		return (float) Math.sqrt(distance2Point(x1, y1, z1, x2, y2, z2));
	}

	public static double distance2(double dx, double dy, double dz) {
		return dx * dx + dy * dy + dz * dz;
	}

	public static double distance(double dx, double dy, double dz) {
		return Math.sqrt(distance2(dx, dy, dz));
	}

	public static double distance2Point(double x1, double y1, double z1, double x2, double y2, double z2) {
		return distance2(x2 - x1, y2 - y1, z2 - z1);
	}

	public static double distancePoint(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Math.sqrt(distance2Point(x1, y1, z1, x2, y2, z2));
	}

	// -------------------------------------------------------------------------------------------------------------- //

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

	// -------------------------------------------------------------------------------------------------------------- //

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

	// -------------------------------------------------------------------------------------------------------------- //

	public static int digitLength(long number, int base) {
		int length = (int) Math.floor(Math.log(Math.abs(number)) / Math.log(base)) + 1;
		return number < 0L ? length + 1 : length;
	}

	public static int digitLength(long number) {
		if (number == 0)
			return 1;

		long n = Math.abs(number);

		int length;
		if (n < 10L) length = 1;
		else if (n < 100L) length = 2;
		else if (n < 1000L) length = 3;
		else if (n < 10000L) length = 4;
		else if (n < 100000L) length = 5;
		else if (n < 1000000L) length = 6;
		else if (n < 10000000L) length = 7;
		else if (n < 100000000L) length = 8;
		else if (n < 1000000000L) length = 9;
		else if (n < 10000000000L) length = 10;
		else if (n < 100000000000L) length = 11;
		else if (n < 1000000000000L) length = 12;
		else if (n < 10000000000000L) length = 13;
		else if (n < 100000000000000L) length = 14;
		else if (n < 1000000000000000L) length = 15;
		else if (n < 10000000000000000L) length = 16;
		else if (n < 100000000000000000L) length = 17;
		else if (n < 1000000000000000000L) length = 18;
		else length = 19;

		return number < 0 ? length + 1 : length;
	}
}
