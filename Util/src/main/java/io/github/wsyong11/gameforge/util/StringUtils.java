package io.github.wsyong11.gameforge.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
}
