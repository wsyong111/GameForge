package io.github.wsyong11.gameforge.util.io;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

@UtilityClass
public class ByteBufferUtils {
	@NotNull
	public static String getString(@NotNull ByteBuffer buffer, @NotNull Charset charset) {
		Objects.requireNonNull(buffer, "buffer is null");
		Objects.requireNonNull(charset, "charset is null");

		int length = buffer.getInt();
		byte[] data = new byte[length];
		buffer.get(data);
		return new String(data, charset);
	}

	public static void putString(@NotNull ByteBuffer buffer, @NotNull String text, @NotNull Charset charset){
		Objects.requireNonNull(buffer, "buffer is null");
		Objects.requireNonNull(text, "text is null");
		Objects.requireNonNull(charset, "charset is null");

		byte[] data = text.getBytes(charset);
		buffer.putInt(data.length);
		buffer.put(data);
	}
}
