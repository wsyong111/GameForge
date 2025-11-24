package io.github.wsyong11.gameforge.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DoubleUtils {
	public static final float FLOAT_EPSILON = 1e-6F;
	public static final double DOUBLE_EPSILON = 1e-6D;

	public static boolean equals(float a, float b) {
		return Math.abs(a - b) <= FLOAT_EPSILON;
	}

	public static boolean equals(double a, double b) {
		return Math.abs(a - b) <= DOUBLE_EPSILON;
	}
}
