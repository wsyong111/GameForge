package io.github.wsyong11.gameforge.util.number;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class NumberUtils {
	private static final char[] HEX = "0123456789ABCDEF".toCharArray();

	public static int digitLength(long number, int base) {
		return (int) Math.floor(Math.log(number) / Math.log(base)) + 1;
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

	public static int hexDigitLength(long number) {
		if (number == 0)
			return 1;

		long n = Math.abs(number);

		int length;
		if (n < 0x10L) length = 1;
		else if (n < 0x100L) length = 2;
		else if (n < 0x1000L) length = 3;
		else if (n < 0x10000L) length = 4;
		else if (n < 0x100000L) length = 5;
		else if (n < 0x1000000L) length = 6;
		else if (n < 0x10000000L) length = 7;
		else if (n < 0x100000000L) length = 8;
		else if (n < 0x1000000000L) length = 9;
		else if (n < 0x10000000000L) length = 10;
		else if (n < 0x100000000000L) length = 11;
		else if (n < 0x1000000000000L) length = 12;
		else if (n < 0x10000000000000L) length = 13;
		else if (n < 0x100000000000000L) length = 14;
		else if (n < 0x1000000000000000L) length = 15;
		else length = 16;

		return number < 0 ? length + 1 : length;
	}

	@NotNull
	public static String toHexFast(long v, int width) {
		char[] buffer = new char[Math.max(width, 16)];

		int pos = buffer.length;

		long value = v;
		do {
			buffer[--pos] = HEX[(int) (value & 0xF)];
			value >>>= 4;
		} while (value != 0);

		// 补零
		while (pos > buffer.length - width)
			buffer[--pos] = '0';

		return new String(buffer, pos, buffer.length - pos);
	}
}
