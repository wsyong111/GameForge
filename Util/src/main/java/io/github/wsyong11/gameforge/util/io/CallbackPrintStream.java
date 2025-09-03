package io.github.wsyong11.gameforge.util.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Objects;

public class CallbackPrintStream extends PrintStream {
	public CallbackPrintStream(@NotNull Callback callback, @NotNull Charset charset) {
		super(new Stream(callback), true, charset);
	}

	public static class Stream extends OutputStream {
		private final Callback callback;
		private final StringBuilder buffer;

		public Stream(@NotNull Callback callback) {
			Objects.requireNonNull(callback, "callback is null");
			this.callback = callback;
			this.buffer = new StringBuilder();
		}

		@Override
		public void write(int b) throws IOException {
			if (b != '\n') {
				this.buffer.append((char) b);
				return;
			}

			try {
				this.callback.onFlush(this.buffer.toString());
			} catch (Exception e) {
				throw new IOException("Callback exception", e);
			} finally {
				this.buffer.setLength(0);
			}
		}
	}

	@NotNull
	public interface Callback {
		void onFlush(@NotNull String text);
	}
}
