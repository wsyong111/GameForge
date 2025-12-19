package io.github.wsyong11.gameforge.util.io;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
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

	@NotNull
	public static InputStream asStream(@NotNull ByteBuffer buffer) {
		Objects.requireNonNull(buffer, "buffer is null");

		ByteBuffer duplicate = buffer.duplicate();
		duplicate.rewind();
		return new ByteBufferInputStream(duplicate);
	}

	private static class ByteBufferInputStream extends InputStream {
		private final ByteBuffer buffer;

		private ByteBufferInputStream(@NotNull ByteBuffer buffer) {
			Objects.requireNonNull(buffer, "buffer is null");
			this.buffer = buffer;
		}

		@Override
		public int read() {
			if (!this.buffer.hasRemaining())
				return -1;

			return this.buffer.get() & 0xFF;
		}

		@Override
		public int read(byte @NotNull [] b, int off, int len) {
			if (!this.buffer.hasRemaining())
				return -1;

			int clampLen=Math.min(len, this.buffer.remaining());
			this.buffer.get(b, off,clampLen);
			return clampLen;
		}
	}
}
