package io.github.wsyong11.gameforge.framework.system.input.event.raw;

import io.github.wsyong11.gameforge.framework.key.KeyAction;
import io.github.wsyong11.gameforge.util.Bit;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class KeyInputEvent implements InputEvent {
	//@formatter:off
	public static final int MODE_SHIFT    = 1;
	public static final int MODE_CONTROL  = 1 << 1;
	public static final int MODE_ALT      = 1 << 2;
	public static final int MODE_SUPER    = 1 << 3;
	public static final int MODE_CAPSLOCK = 1 << 4;
	public static final int MODE_NUMLOCK  = 1 << 5;

	@ModeMask
	public static final int MODE_ALL = MODE_SHIFT | MODE_CONTROL | MODE_ALT | MODE_SUPER | MODE_CAPSLOCK | MODE_NUMLOCK;
	//@formatter:on

	@MagicConstant(flags = {
		MODE_SHIFT,
		MODE_CONTROL,
		MODE_ALT,
		MODE_SUPER,
		MODE_CAPSLOCK,
		MODE_NUMLOCK
	})
	public @interface ModeMask {
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private final KeyAction action;

	@ModeMask
	private final int mods;

	public KeyInputEvent(@NotNull KeyAction action, int mods) {
		Objects.requireNonNull(action, "action is null");

		this.action = action;
		this.mods = mods;
	}

	@NotNull
	public KeyAction getAction() {
		return this.action;
	}

	@ModeMask
	public int getMods() {
		return this.mods;
	}

	/**
	 * 检查Shift是否被一起按下
	 *
	 * @return Shift是否被按下
	 */
	public boolean isShiftPress() {
		return Bit.has(this.mods, MODE_SHIFT);
	}

	/**
	 * 检查CTRL是否被一起按下
	 *
	 * @return CTRL是否被按下
	 */
	public boolean isControlPress() {
		return Bit.has(this.mods, MODE_CONTROL);
	}

	/**
	 * 检查Alt是否被一起按下
	 *
	 * @return Alt是否被按下
	 */
	public boolean isAltPress() {
		return Bit.has(this.mods, MODE_ALT);
	}

	/**
	 * 检查Super是否被一起按下
	 *
	 * @return Super是否被按下
	 */
	public boolean isSuperPress() {
		return Bit.has(this.mods, MODE_SUPER);
	}

	/**
	 * 检查大写锁定是否被一起按下
	 *
	 * @return 大写锁定是否被按下
	 */
	public boolean isCapsLockPress() {
		return Bit.has(this.mods, MODE_CAPSLOCK);
	}

	/**
	 * 检查小键盘锁定是否被一起按下
	 *
	 * @return 小键盘锁定是否被按下
	 */
	public boolean isNumLockPress() {
		return Bit.has(this.mods, MODE_NUMLOCK);
	}

	protected void toString(@NotNull List<String> data) {
		Objects.requireNonNull(data, "data is null");

		if (this.isShiftPress()) data.add("SHIFT");
		if (this.isControlPress()) data.add("CTRL");
		if (this.isAltPress()) data.add("ALT");
		if (this.isSuperPress()) data.add("SUPER");
		if (this.isCapsLockPress()) data.add("CAPS_LCK");
		if (this.isNumLockPress()) data.add("NUM_LCK");
	}

	@NotNull
	@Override
	public String toString() {
		List<String> data = new ArrayList<>(7);
		this.toString(data);

		return "Key[" + this.action + ", [" + String.join(", ", data) + "]]";
	}
}
