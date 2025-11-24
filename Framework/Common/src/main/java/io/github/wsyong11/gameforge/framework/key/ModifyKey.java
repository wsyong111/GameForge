package io.github.wsyong11.gameforge.framework.key;

import io.github.wsyong11.gameforge.util.Bit;
import lombok.experimental.UtilityClass;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@UtilityClass
public class ModifyKey {
	//@formatter:off
	public static final int SHIFT    = 1;
	public static final int CONTROL  = 1 << 1;
	public static final int ALT      = 1 << 2;
	public static final int SUPER    = 1 << 3;
	public static final int CAPSLOCK = 1 << 4;
	public static final int NUMLOCK  = 1 << 5;

	@Mask
	public static final int ALL = SHIFT | CONTROL | ALT | SUPER | CAPSLOCK | NUMLOCK;
	//@formatter:on

	@MagicConstant(flags = {
		SHIFT,
		CONTROL,
		ALT,
		SUPER,
		CAPSLOCK,
		NUMLOCK
	})
	public @interface Mask {
	}

	/**
	 * 检查Shift是否被一起按下
	 *
	 * @return Shift是否被按下
	 */
	public static boolean isShift(@Mask int modifier) {
		return Bit.has(modifier, SHIFT);
	}

	/**
	 * 检查CTRL是否被一起按下
	 *
	 * @return CTRL是否被按下
	 */
	public static boolean isControl(@Mask int modifier) {
		return Bit.has(modifier, CONTROL);
	}

	/**
	 * 检查Alt是否被一起按下
	 *
	 * @return Alt是否被按下
	 */
	public static boolean isAlt(@Mask int modifier) {
		return Bit.has(modifier, ALT);
	}

	/**
	 * 检查Super是否被一起按下
	 *
	 * @return Super是否被按下
	 */
	public static boolean isSuper(@Mask int modifier) {
		return Bit.has(modifier, SUPER);
	}

	/**
	 * 检查大写锁定是否被一起按下
	 *
	 * @return 大写锁定是否被按下
	 */
	public static boolean isCapsLock(@Mask int modifier) {
		return Bit.has(modifier, CAPSLOCK);
	}

	/**
	 * 检查小键盘锁定是否被一起按下
	 *
	 * @return 小键盘锁定是否被按下
	 */
	public static boolean isNumLock(@Mask int modifier) {
		return Bit.has(modifier, NUMLOCK);
	}

	public static boolean isValid(@Mask int modifier) {
		return Bit.isValid(modifier, ALL);
	}

	private static final Map<Integer, String> STRING_CAST = Map.of(
		SHIFT, "SHIFT",
		CONTROL, "CONTROL",
		ALT, "ALT",
		SUPER, "SUPER",
		CAPSLOCK, "CAPSLOCK",
		NUMLOCK, "NUMLOCK"
	);

	@NotNull
	public static String toString(@Mask int modifier) {
		return Bit.toString(modifier, STRING_CAST);

//		List<String> list = new ArrayList<>(7);
//
//		if (isShift(modifier)) list.add("SHIFT");
//		if (isControl(modifier)) list.add("CONTROL");
//		if (isAlt(modifier)) list.add("ALT");
//		if (isSuper(modifier)) list.add("SUPER");
//		if (isCapsLock(modifier)) list.add("CAPSLOCK");
//		if (isNumLock(modifier)) list.add("NUMLOCK");
//
//		return String.join(" | ", list);
	}
}
