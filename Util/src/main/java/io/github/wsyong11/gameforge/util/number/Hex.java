package io.github.wsyong11.gameforge.util.number;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.Objects;

@UtilityClass
public class Hex {
	private static final char[] HEX = "0123456789ABCDEF".toCharArray();

	@NonNull
	public static String encodeHex(byte @NotNull [] data) {
		return encodeHex(data, 0, data.length);
	}

	@NotNull
	public static String encodeHex(@NotNull ByteBuffer buffer) {
		Objects.requireNonNull(buffer, "buffer is null");

		if (buffer.hasArray()) {
			int offset = buffer.arrayOffset() + buffer.position();

			return encodeHex(
				buffer.array(),
				offset,
				buffer.remaining()
			);
		}

		ByteBuffer buf = buffer.duplicate();

		char[] result = new char[buf.remaining() * 2];

		int i = 0;
		while (buf.hasRemaining()) {
			int v = buf.get() & 0xFF;

			result[i++] = HEX[v >>> 4];
			result[i++] = HEX[v & 0x0F];
		}

		return new String(result);
	}

	@NonNull
	public static String encodeHex(byte @NotNull [] data, int offset, int length) {
		Objects.requireNonNull(data, "data is null");
		Objects.checkFromIndexSize(offset, length, data.length);

		char[] result = new char[data.length * 2];

		for (int i = 0; i < data.length; i++) {
			int v = data[i] & 0xFF;
			result[i * 2] = HEX[v >>> 4];
			result[i * 2 + 1] = HEX[v & 0x0F];
		}

		return new String(result);
	}

	public static byte @NotNull [] decodeHex(@NotNull String hex) {
		Objects.requireNonNull(hex, "hex is null");

		int len = hex.length();
		byte[] result = new byte[len / 2];

		for (int i = 0; i < len; i += 2) {
			int upperBit = fromHex(hex.charAt(i)) << 4;
			int lowerBit = fromHex(hex.charAt(i + 1));

			result[i / 2] = (byte) (upperBit | lowerBit);
		}
		return result;
	}

	private static int fromHex(char c) {
		if (c >= '0' && c <= '9')
			return c - '0';
		if (c >= 'A' && c <= 'F')
			return c - 'A' + 10;
		if (c >= 'a' && c <= 'f')
			return c - 'a' + 10;

		throw new IllegalArgumentException("Invalid hex char: " + c);
	}

	@NotNull
	public static String toHex(int v) {
		return Integer.toHexString(v).toUpperCase();
	}

	@NotNull
	public static String toHex(long v) {
		return Long.toHexString(v).toUpperCase();
	}

	@NotNull
	public static String toHex(int v, int digits) {
		return String.format("%0" + digits + "X", v);
	}

	public static int fromHexInt(@NotNull String hex) {
		Objects.requireNonNull(hex, "hex is null");
		return Integer.parseUnsignedInt(hex, 16);
	}

	public static long fromHexLong(@NotNull String hex) {
		Objects.requireNonNull(hex, "hex is null");
		return Long.parseUnsignedLong(hex, 16);
	}

	public static boolean isHex(@Nullable String s) {
		if (s == null || s.isEmpty())
			return false;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (!((c >= '0' && c <= '9') ||
				(c >= 'a' && c <= 'f') ||
				(c >= 'A' && c <= 'F'))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidByteHex(@NotNull String hex) {
		Objects.requireNonNull(hex, "hex is null");
		return hex.length() % 2 == 0 && isHex(hex);
	}
}
