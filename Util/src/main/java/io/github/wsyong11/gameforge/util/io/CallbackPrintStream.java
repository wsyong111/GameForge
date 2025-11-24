package io.github.wsyong11.gameforge.util.io;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Objects;

public class CallbackPrintStream extends PrintStream {
	public CallbackPrintStream(@NotNull Callback callback, @NotNull Charset charset) {
		super(new Stream(callback, charset), true, charset);
	}

	public static class Stream extends OutputStream {
		private final Callback callback;
		private final Charset charset;

		private final ByteArrayOutputStream byteBuffer;

		public Stream(@NotNull Callback callback, @NotNull Charset charset) {
			Objects.requireNonNull(callback, "callback is null");
			Objects.requireNonNull(charset, "charset is null");

			this.callback = callback;
			this.charset = charset;

			this.byteBuffer = new ByteArrayOutputStream();
		}

		@Override
		public void write(int b) throws IOException {
			if (b != '\n') {
				this.byteBuffer.write(b);
				return;
			}

			String str = this.byteBuffer.toString(this.charset);
			this.byteBuffer.reset();

			try {
				this.callback.onFlush(str);
			} catch (Exception e) {
				throw new IOException("Callback exception", e);
			}
		}
	}

	@NotNull
	public interface Callback {
		void onFlush(@NotNull String text);
	}
}
