package io.github.wsyong11.gameforge.framework.system.window.impl.glfw;

import io.github.wsyong11.gameforge.framework.KeyCode;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.window.input.KeyAction;
import io.github.wsyong11.gameforge.framework.system.window.input.KeyInputEvent;
import io.github.wsyong11.gameforge.util.Bit;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

@UtilityClass
public class GLFWInputUtils {
	private static final Logger LOGGER = Log.getLogger();

	private static final Map<Integer, KeyCode> GLFW_TO_KEYCODE = new HashMap<>();

	static {
		// 字母 A-Z
		GLFW_TO_KEYCODE.put(GLFW_KEY_A, KeyCode.A);
		GLFW_TO_KEYCODE.put(GLFW_KEY_B, KeyCode.B);
		GLFW_TO_KEYCODE.put(GLFW_KEY_C, KeyCode.C);
		GLFW_TO_KEYCODE.put(GLFW_KEY_D, KeyCode.D);
		GLFW_TO_KEYCODE.put(GLFW_KEY_E, KeyCode.E);
		GLFW_TO_KEYCODE.put(GLFW_KEY_F, KeyCode.F);
		GLFW_TO_KEYCODE.put(GLFW_KEY_G, KeyCode.G);
		GLFW_TO_KEYCODE.put(GLFW_KEY_H, KeyCode.H);
		GLFW_TO_KEYCODE.put(GLFW_KEY_I, KeyCode.I);
		GLFW_TO_KEYCODE.put(GLFW_KEY_J, KeyCode.J);
		GLFW_TO_KEYCODE.put(GLFW_KEY_K, KeyCode.K);
		GLFW_TO_KEYCODE.put(GLFW_KEY_L, KeyCode.L);
		GLFW_TO_KEYCODE.put(GLFW_KEY_M, KeyCode.M);
		GLFW_TO_KEYCODE.put(GLFW_KEY_N, KeyCode.N);
		GLFW_TO_KEYCODE.put(GLFW_KEY_O, KeyCode.O);
		GLFW_TO_KEYCODE.put(GLFW_KEY_P, KeyCode.P);
		GLFW_TO_KEYCODE.put(GLFW_KEY_Q, KeyCode.Q);
		GLFW_TO_KEYCODE.put(GLFW_KEY_R, KeyCode.R);
		GLFW_TO_KEYCODE.put(GLFW_KEY_S, KeyCode.S);
		GLFW_TO_KEYCODE.put(GLFW_KEY_T, KeyCode.T);
		GLFW_TO_KEYCODE.put(GLFW_KEY_U, KeyCode.U);
		GLFW_TO_KEYCODE.put(GLFW_KEY_V, KeyCode.V);
		GLFW_TO_KEYCODE.put(GLFW_KEY_W, KeyCode.W);
		GLFW_TO_KEYCODE.put(GLFW_KEY_X, KeyCode.X);
		GLFW_TO_KEYCODE.put(GLFW_KEY_Y, KeyCode.Y);
		GLFW_TO_KEYCODE.put(GLFW_KEY_Z, KeyCode.Z);

		// 数字键 0-9
		GLFW_TO_KEYCODE.put(GLFW_KEY_0, KeyCode.DIGIT_0);
		GLFW_TO_KEYCODE.put(GLFW_KEY_1, KeyCode.DIGIT_1);
		GLFW_TO_KEYCODE.put(GLFW_KEY_2, KeyCode.DIGIT_2);
		GLFW_TO_KEYCODE.put(GLFW_KEY_3, KeyCode.DIGIT_3);
		GLFW_TO_KEYCODE.put(GLFW_KEY_4, KeyCode.DIGIT_4);
		GLFW_TO_KEYCODE.put(GLFW_KEY_5, KeyCode.DIGIT_5);
		GLFW_TO_KEYCODE.put(GLFW_KEY_6, KeyCode.DIGIT_6);
		GLFW_TO_KEYCODE.put(GLFW_KEY_7, KeyCode.DIGIT_7);
		GLFW_TO_KEYCODE.put(GLFW_KEY_8, KeyCode.DIGIT_8);
		GLFW_TO_KEYCODE.put(GLFW_KEY_9, KeyCode.DIGIT_9);

		// 功能键 F1-F12
		GLFW_TO_KEYCODE.put(GLFW_KEY_F1, KeyCode.F1);
		GLFW_TO_KEYCODE.put(GLFW_KEY_F2, KeyCode.F2);
		GLFW_TO_KEYCODE.put(GLFW_KEY_F3, KeyCode.F3);
		GLFW_TO_KEYCODE.put(GLFW_KEY_F4, KeyCode.F4);
		GLFW_TO_KEYCODE.put(GLFW_KEY_F5, KeyCode.F5);
		GLFW_TO_KEYCODE.put(GLFW_KEY_F6, KeyCode.F6);
		GLFW_TO_KEYCODE.put(GLFW_KEY_F7, KeyCode.F7);
		GLFW_TO_KEYCODE.put(GLFW_KEY_F8, KeyCode.F8);
		GLFW_TO_KEYCODE.put(GLFW_KEY_F9, KeyCode.F9);
		GLFW_TO_KEYCODE.put(GLFW_KEY_F10, KeyCode.F10);
		GLFW_TO_KEYCODE.put(GLFW_KEY_F11, KeyCode.F11);
		GLFW_TO_KEYCODE.put(GLFW_KEY_F12, KeyCode.F12);

		// 控制键
		GLFW_TO_KEYCODE.put(GLFW_KEY_ESCAPE, KeyCode.ESCAPE);
		GLFW_TO_KEYCODE.put(GLFW_KEY_TAB, KeyCode.TAB);
		GLFW_TO_KEYCODE.put(GLFW_KEY_CAPS_LOCK, KeyCode.CAPS_LOCK);
		GLFW_TO_KEYCODE.put(GLFW_KEY_NUM_LOCK, KeyCode.NUM_LOCK);
		GLFW_TO_KEYCODE.put(GLFW_KEY_LEFT_SHIFT, KeyCode.LEFT_SHIFT);
		GLFW_TO_KEYCODE.put(GLFW_KEY_RIGHT_SHIFT, KeyCode.RIGHT_SHIFT);
		GLFW_TO_KEYCODE.put(GLFW_KEY_LEFT_CONTROL, KeyCode.LEFT_CONTROL);
		GLFW_TO_KEYCODE.put(GLFW_KEY_RIGHT_CONTROL, KeyCode.RIGHT_CONTROL);
		GLFW_TO_KEYCODE.put(GLFW_KEY_LEFT_ALT, KeyCode.LEFT_ALT);
		GLFW_TO_KEYCODE.put(GLFW_KEY_RIGHT_ALT, KeyCode.RIGHT_ALT);
		GLFW_TO_KEYCODE.put(GLFW_KEY_ENTER, KeyCode.ENTER);
		GLFW_TO_KEYCODE.put(GLFW_KEY_BACKSPACE, KeyCode.BACKSPACE);
		GLFW_TO_KEYCODE.put(GLFW_KEY_SPACE, KeyCode.SPACE);
		GLFW_TO_KEYCODE.put(GLFW_KEY_DELETE, KeyCode.DELETE);
		GLFW_TO_KEYCODE.put(GLFW_KEY_INSERT, KeyCode.INSERT);
		GLFW_TO_KEYCODE.put(GLFW_KEY_HOME, KeyCode.HOME);
		GLFW_TO_KEYCODE.put(GLFW_KEY_END, KeyCode.END);
		GLFW_TO_KEYCODE.put(GLFW_KEY_PAGE_UP, KeyCode.PAGE_UP);
		GLFW_TO_KEYCODE.put(GLFW_KEY_PAGE_DOWN, KeyCode.PAGE_DOWN);
		GLFW_TO_KEYCODE.put(GLFW_KEY_UP, KeyCode.ARROW_UP);
		GLFW_TO_KEYCODE.put(GLFW_KEY_DOWN, KeyCode.ARROW_DOWN);
		GLFW_TO_KEYCODE.put(GLFW_KEY_LEFT, KeyCode.ARROW_LEFT);
		GLFW_TO_KEYCODE.put(GLFW_KEY_RIGHT, KeyCode.ARROW_RIGHT);
		GLFW_TO_KEYCODE.put(GLFW_KEY_LEFT_SUPER, KeyCode.LEFT_SUPER);
		GLFW_TO_KEYCODE.put(GLFW_KEY_RIGHT_SUPER, KeyCode.RIGHT_SUPER);
		GLFW_TO_KEYCODE.put(GLFW_KEY_MENU, KeyCode.MENU);

		// 符号键
		GLFW_TO_KEYCODE.put(GLFW_KEY_MINUS, KeyCode.MINUS);
		GLFW_TO_KEYCODE.put(GLFW_KEY_EQUAL, KeyCode.EQUAL);
		GLFW_TO_KEYCODE.put(GLFW_KEY_LEFT_BRACKET, KeyCode.LEFT_BRACKET);
		GLFW_TO_KEYCODE.put(GLFW_KEY_RIGHT_BRACKET, KeyCode.RIGHT_BRACKET);
		GLFW_TO_KEYCODE.put(GLFW_KEY_BACKSLASH, KeyCode.BACKSLASH);
		GLFW_TO_KEYCODE.put(GLFW_KEY_SEMICOLON, KeyCode.SEMICOLON);
		GLFW_TO_KEYCODE.put(GLFW_KEY_APOSTROPHE, KeyCode.APOSTROPHE);
		GLFW_TO_KEYCODE.put(GLFW_KEY_GRAVE_ACCENT, KeyCode.GRAVE_ACCENT);
		GLFW_TO_KEYCODE.put(GLFW_KEY_COMMA, KeyCode.COMMA);
		GLFW_TO_KEYCODE.put(GLFW_KEY_PERIOD, KeyCode.PERIOD);
		GLFW_TO_KEYCODE.put(GLFW_KEY_SLASH, KeyCode.SLASH);

		// 小键盘
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_0, KeyCode.NUMPAD_0);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_1, KeyCode.NUMPAD_1);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_2, KeyCode.NUMPAD_2);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_3, KeyCode.NUMPAD_3);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_4, KeyCode.NUMPAD_4);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_5, KeyCode.NUMPAD_5);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_6, KeyCode.NUMPAD_6);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_7, KeyCode.NUMPAD_7);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_8, KeyCode.NUMPAD_8);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_9, KeyCode.NUMPAD_9);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_ENTER, KeyCode.NUMPAD_ENTER);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_DECIMAL, KeyCode.NUMPAD_DECIMAL);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_ADD, KeyCode.NUMPAD_ADD);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_SUBTRACT, KeyCode.NUMPAD_SUBTRACT);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_MULTIPLY, KeyCode.NUMPAD_MULTIPLY);
		GLFW_TO_KEYCODE.put(GLFW_KEY_KP_DIVIDE, KeyCode.NUMPAD_DIVIDE);

		GLFW_TO_KEYCODE.put(GLFW_KEY_PRINT_SCREEN, KeyCode.PRINT_SCREEN);
		GLFW_TO_KEYCODE.put(GLFW_KEY_SCROLL_LOCK, KeyCode.SCROLL_LOCK);
		GLFW_TO_KEYCODE.put(GLFW_KEY_PAUSE, KeyCode.PAUSE);

		for (KeyCode code : KeyCode.values()) {
			if (code==KeyCode.UNKNOWN)
				continue;

			if (!GLFW_TO_KEYCODE.containsValue(code))
				throw new IllegalArgumentException("Missing cast " + code);
		}
	}

	@KeyInputEvent.ModeMask
	public static int castMods(int mods) {
		int result = 0;
		if (Bit.has(mods, GLFW_MOD_SHIFT)) result |= KeyInputEvent.MODE_SHIFT;
		if (Bit.has(mods, GLFW_MOD_CONTROL)) result |= KeyInputEvent.MODE_CONTROL;
		if (Bit.has(mods, GLFW_MOD_ALT)) result |= KeyInputEvent.MODE_ALT;
		if (Bit.has(mods, GLFW_MOD_SUPER)) result |= KeyInputEvent.MODE_SUPER;
		if (Bit.has(mods, GLFW_MOD_CAPS_LOCK)) result |= KeyInputEvent.MODE_CAPSLOCK;
		if (Bit.has(mods, GLFW_MOD_NUM_LOCK)) result |= KeyInputEvent.MODE_NUMLOCK;
		return result;
	}

	@NotNull
	public static KeyCode castKeyCode(int code) {
		KeyCode keyCode = GLFW_TO_KEYCODE.getOrDefault(code, KeyCode.UNKNOWN);
		if (keyCode == KeyCode.UNKNOWN)
			LOGGER.debug("Unknown glfw key code: {}", code);

		return keyCode;
	}

	@NotNull
	public static KeyAction castAction(int action) {
		return switch (action) {
			case GLFW_PRESS -> KeyAction.DOWN;
			case GLFW_RELEASE -> KeyAction.UP;
			case GLFW_REPEAT -> KeyAction.HOLD;
			default -> throw new IllegalArgumentException("Unknown glfw action " + action);
		};
	}
}
