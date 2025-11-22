package io.github.wsyong11.gameforge.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.IterableUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
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
	public static String joinNonNull(@NotNull CharSequence delimiter, @Nullable String... elements) {
		return Arrays.stream(elements)
		             .filter(Objects::nonNull)
		             .collect(Collectors.joining(delimiter));
	}
}
