package io.github.wsyong11.gameforge.framework;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/*

 */
public enum KeyCode {
	UNKNOWN(0x0000, '\0', "unknown"),

	// 字母键 (0x00FF)
	//@formatter:off
	A(0x0001, 'a', "KeyA"),
	B(0x0002, 'b', "KeyB"),
	C(0x0003, 'c', "KeyC"),
	D(0x0004, 'd', "KeyD"),
	E(0x0005, 'e', "KeyE"),
	F(0x0006, 'f', "KeyF"),
	G(0x0007, 'g', "KeyG"),
	H(0x0008, 'h', "KeyH"),
	I(0x0009, 'i', "KeyI"),
	J(0x000A, 'j', "KeyJ"),
	K(0x000B, 'k', "KeyK"),
	L(0x000C, 'l', "KeyL"),
	M(0x000D, 'm', "KeyM"),
	N(0x000E, 'n', "KeyN"),
	O(0x000F, 'o', "KeyO"),
	P(0x0010, 'p', "KeyP"),
	Q(0x0011, 'q', "KeyQ"),
	R(0x0012, 'r', "KeyR"),
	S(0x0013, 's', "KeyS"),
	T(0x0014, 't', "KeyT"),
	U(0x0015, 'u', "KeyU"),
	V(0x0016, 'v', "KeyV"),
	W(0x0017, 'w', "KeyW"),
	X(0x0018, 'x', "KeyX"),
	Y(0x0019, 'y', "KeyY"),
	Z(0x001A, 'z', "KeyZ"),

	// 数字键 (0x01FF)
	DIGIT_0(0x0100, '0', "Digit0"),
	DIGIT_1(0x0101, '1', "Digit1"),
	DIGIT_2(0x0102, '2', "Digit2"),
	DIGIT_3(0x0103, '3', "Digit3"),
	DIGIT_4(0x0104, '4', "Digit4"),
	DIGIT_5(0x0105, '5', "Digit5"),
	DIGIT_6(0x0106, '6', "Digit6"),
	DIGIT_7(0x0107, '7', "Digit7"),
	DIGIT_8(0x0108, '8', "Digit8"),
	DIGIT_9(0x0109, '9', "Digit9"),

	// 功能键 (0x02FF)
	F1 (0x0200, '\0', "F1"),
	F2 (0x0201, '\0', "F2"),
	F3 (0x0202, '\0', "F3"),
	F4 (0x0203, '\0', "F4"),
	F5 (0x0204, '\0', "F5"),
	F6 (0x0205, '\0', "F6"),
	F7 (0x0206, '\0', "F7"),
	F8 (0x0207, '\0', "F8"),
	F9 (0x0208, '\0', "F9"),
	F10(0x0209, '\0', "F10"),
	F11(0x020A, '\0', "F11"),
	F12(0x020B, '\0', "F12"),

	// 控制键 (0x03FF)
	ESCAPE       (0x0300, '\0', "Escape"),
	TAB          (0x0301, '\t', "Tab"),
	CAPS_LOCK    (0x0302, '\0', "CapsLock"),
	NUM_LOCK     (0x0303, '\0', "NumLock"),
	LEFT_SHIFT   (0x0304, '\0', "ShiftLeft"),
	RIGHT_SHIFT  (0x0305, '\0', "ShiftRight"),
	LEFT_CONTROL (0x0306, '\0', "ControlLeft"),
	RIGHT_CONTROL(0x0307, '\0', "ControlRight"),
	LEFT_ALT     (0x0308, '\0', "AltLeft"),
	RIGHT_ALT    (0x0309, '\0', "AltRight"),
	ENTER        (0x030A, '\n', "Enter"),
	BACKSPACE    (0x030B, '\b', "Backspace"),
	SPACE        (0x030C, ' ', "Space"),
	DELETE       (0x030D, '\0', "Delete"),
	INSERT       (0x030E, '\0', "Insert"),
	HOME         (0x030F, '\0', "Home"),
	END          (0x0310, '\0', "End"),
	PAGE_UP      (0x0311, '\0', "PageUp"),
	PAGE_DOWN    (0x0312, '\0', "PageDown"),
	ARROW_UP     (0x0313, '\0', "ArrowUp"),
	ARROW_DOWN   (0x0314, '\0', "ArrowDown"),
	ARROW_LEFT   (0x0315, '\0', "ArrowLeft"),
	ARROW_RIGHT  (0x0316, '\0', "ArrowRight"),
	LEFT_SUPER   (0x0317, '\0', "SuperLeft"),
	RIGHT_SUPER  (0x0318, '\0', "SuperRight"),
	MENU         (0x0319, '\0', "Menu"),

	// 符号键 (0x04FF)
	MINUS        (0x0400, '-', "Minus"),
	EQUAL        (0x0401, '=', "Equal"),
	LEFT_BRACKET (0x0402, '[', "BracketLeft"),
	RIGHT_BRACKET(0x0403, ']', "BracketRight"),
	BACKSLASH    (0x0404, '\\', "Backslash"),
	SEMICOLON    (0x0405, ';', "Semicolon"),
	APOSTROPHE   (0x0406, '\'', "Quote"),
	GRAVE_ACCENT (0x0407, '`', "Backquote"),
	COMMA        (0x0408, ',', "Comma"),
	PERIOD       (0x0409, '.', "Period"),
	SLASH        (0x040A, '/', "Slash"),

	// 小键盘 (0x05FF)
	NUMPAD_0       (0x0500, '0', "Numpad0"),
	NUMPAD_1       (0x0501, '1', "Numpad1"),
	NUMPAD_2       (0x0502, '2', "Numpad2"),
	NUMPAD_3       (0x0503, '3', "Numpad3"),
	NUMPAD_4       (0x0504, '4', "Numpad4"),
	NUMPAD_5       (0x0505, '5', "Numpad5"),
	NUMPAD_6       (0x0506, '6', "Numpad6"),
	NUMPAD_7       (0x0507, '7', "Numpad7"),
	NUMPAD_8       (0x0508, '8', "Numpad8"),
	NUMPAD_9       (0x0509, '9', "Numpad9"),
	NUMPAD_DECIMAL (0x0510, '.', "NumpadDecimal"),
	NUMPAD_ADD     (0x0511, '+', "NumpadAdd"),
	NUMPAD_SUBTRACT(0x0512, '-', "NumpadSubtract"),
	NUMPAD_MULTIPLY(0x0513, '*', "NumpadMultiply"),
	NUMPAD_DIVIDE  (0x0514, '/', "NumpadDivide"),
	NUMPAD_ENTER   (0x0515,'\n',"NumpadEnter"),

	// 特殊键 (0x06FF)
	PRINT_SCREEN(0x0600, '\0', "PrintScreen"),
	SCROLL_LOCK (0x0601, '\0', "ScrollLock"),
	PAUSE       (0x0602, '\0', "Pause")
	;//@formatter:on

	static {
		BitSet set = new BitSet();
		for (KeyCode code : values()) {
			int codeIndex = code.getCode();
			if (set.get(codeIndex))
				throw new IllegalArgumentException("Multi define key code " + codeIndex + ": " + code);
			set.set(codeIndex);
		}
	}

	private static final Map<Integer, KeyCode> CODE_MAP = Arrays
		.stream(values())
		.collect(Collectors.toMap(KeyCode::getCode, Function.identity()));

	@NotNull
	public static KeyCode fromCode(int code) {
		return CODE_MAP.getOrDefault(code, UNKNOWN);
	}

	private final int code;
	private final char charCode;
	private final String name;

	KeyCode(int code, char charCode, String name) {
		this.code = code;
		this.charCode = charCode;
		this.name = name;
	}

	public int getCode() {
		return this.code;
	}

	public char getCharCode() {
		return this.charCode;
	}

	@NotNull
	public String getName() {
		return this.name;
	}

	public boolean isLetter() {
		return this.code >= 0x0000 && this.code <= 0x00FF;
	}

	public boolean isNumber() {
		return this.code >= 0x0100 && this.code <= 0x01FF;
	}

	public boolean isFunction() {
		return this.code >= 0x0200 && this.code <= 0x02FF;
	}

	public boolean isControl() {
		return this.code >= 0x0300 && this.code <= 0x03FF;
	}

	public boolean isSymbol() {
		return this.code >= 0x0400 && this.code <= 0x04FF;
	}

	public boolean isKeypad() {
		return this.code >= 0x0500 && this.code <= 0x05FF;
	}

	public boolean isSpecial() {
		return this.code >= 0x0600 && this.code <= 0x06FF;
	}

	@Override
	public String toString() {
		return "Key[" + this.name + "]";
	}
}

