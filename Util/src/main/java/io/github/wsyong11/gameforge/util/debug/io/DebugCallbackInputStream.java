package io.github.wsyong11.gameforge.util.debug.io;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

public class DebugCallbackInputStream extends InputStream {
	private final Callback callback;

	private InputStream in;
	private long position;

	public DebugCallbackInputStream(@NotNull InputStream in, @NotNull Callback callback) {
		Objects.requireNonNull(in, "in is null");
		Objects.requireNonNull(callback, "callback is null");

		this.in = in;
		this.callback = callback;

		this.position = 0L;
	}

	@Override
	public int read(byte @NotNull [] b) throws IOException {
		int consumed = this.in.read(b);
		if (consumed == -1)
			return consumed;

		int length = b.length;
		if (consumed == 0) {
			this.callback.onRead(this.position, 0, length, ArrayUtils.EMPTY_BYTE_ARRAY);
		} else {
			byte[] data = Arrays.copyOf(b, consumed);
			this.callback.onRead(this.position, 0, length, data);
		}

		this.position += consumed;

		return consumed;
	}

	@Override
	public int read(byte @NotNull [] b, int off, int len) throws IOException {
		int consumed = this.in.read(b, off, len);
		return consumed;
	}

	@Override
	public byte[] readAllBytes() throws IOException {
		return this.in.readAllBytes();
	}

	@Override
	public byte[] readNBytes(int len) throws IOException {
		return this.in.readNBytes(len);
	}

	@Override
	public int readNBytes(byte[] b, int off, int len) throws IOException {
		return this.in.readNBytes(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return this.in.skip(n);
	}

	@Override
	public void skipNBytes(long n) throws IOException {
		this.in.skipNBytes(n);
	}

	@Override
	public int available() throws IOException {
		return this.in.available();
	}

	@Override
	public void close() throws IOException {
		this.in.close();
	}

	@Override
	public synchronized void mark(int readLimit) {
		this.in.mark(readLimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		this.in.reset();
	}

	@Override
	public boolean markSupported() {
		return this.in.markSupported();
	}

	@Override
	public long transferTo(@NotNull OutputStream out) throws IOException {
		return this.in.transferTo(out);
	}

	@Override
	public int read() throws IOException {
		return 0;
	}

	public interface Callback {
		default void onRead(long totalRead, int offset, int length, byte[] data) {
		}

		default void onMark(long markPosition, int readLimit) {
		}

		default void onReset() {
		}

		default void onClose() {
		}

		default void onSkip(long skipped) {
		}
	}
}
