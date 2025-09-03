package io.github.wsyong11.gameforge.framework.text.rich;

import io.github.wsyong11.gameforge.util.Bit;
import lombok.experimental.UtilityClass;
import org.intellij.lang.annotations.MagicConstant;

@UtilityClass
public class TextStyleModifier {
	//@formatter:off
	public static final int BOLD          = 1;
	public static final int ITALIC        = 1 << 1;
	public static final int UNDERLINED    = 1 << 2;
	public static final int STRIKETHROUGH = 1 << 3;
	public static final int OBFUSCATED    = 1 << 4;
 	//@formatter:on

	@Mask
	public static final int ALL = BOLD | ITALIC | UNDERLINED | STRIKETHROUGH | OBFUSCATED;

	@MagicConstant(flags = {
		BOLD,
		ITALIC,
		UNDERLINED,
		STRIKETHROUGH,
		OBFUSCATED
	})
	public @interface Mask {
	}

	public static boolean isBold(@Mask int modifiers) {
		return Bit.has(modifiers, BOLD);
	}

	public static boolean isItalic(@Mask int modifiers) {
		return Bit.has(modifiers, ITALIC);
	}

	public static boolean isUnderlined(@Mask int modifiers) {
		return Bit.has(modifiers, UNDERLINED);
	}

	public static boolean isStrikethrough(@Mask int modifiers) {
		return Bit.has(modifiers, STRIKETHROUGH);
	}

	public static boolean isObfuscated(@Mask int modifiers) {
		return Bit.has(modifiers, OBFUSCATED);
	}
}
