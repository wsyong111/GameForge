package io.github.wsyong11.gameforge.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class StringUtils {
	public static boolean isLetter(@NotNull CharSequence sequence) {
		Objects.requireNonNull(sequence, "sequence is null");
		return sequence.codePoints().allMatch(Character::isLetter);
	}

	public static boolean isLetterOrDigit(@NotNull CharSequence sequence) {
		Objects.requireNonNull(sequence, "sequence is null");
		return sequence.codePoints().allMatch(Character::isLetterOrDigit);
	}

	@NotNull
	public static String formatRange(int min, int max) {
		if (min == max)
			return String.valueOf(min);
		else if (max == Integer.MAX_VALUE)
			return min + "+";
		else
			return min + ".." + max;
	}

	@NotNull
	public static String formatRange(long min, long max) {
		if (min == max)
			return String.valueOf(min);
		else if (max == Long.MAX_VALUE)
			return min + "+";
		else
			return min + ".." + max;
	}

	@NotNull
	public static String formatRange(float min, float max) {
		if (DoubleUtils.equals(min, max))
			return String.valueOf(min);
		else if (DoubleUtils.equals(max, Float.MAX_VALUE))
			return min + "+";
		else
			return min + ".." + max;
	}

	@NotNull
	public static String formatRange(double min, double max) {
		if (DoubleUtils.equals(min, max))
			return String.valueOf(min);
		else if (DoubleUtils.equals(max, Double.MAX_VALUE))
			return min + "+";
		else
			return min + ".." + max;
	}

	@NotNull
	public static String joinNonNull(@NotNull CharSequence delimiter, @Nullable String... elements) {
		return Arrays.stream(elements)
		             .filter(Objects::nonNull)
		             .collect(Collectors.joining(delimiter));
	}

	@NotNull
	public static String wrapWithQuotes(@NotNull String t) {
		Objects.requireNonNull(t, "t is null");
		return t.length() > 1 ? "\"" + t + "\"" : "'" + t + "'";
	}
}
