package io.github.wsyong11.gameforge.util.debug.io;

import org.jetbrains.annotations.NotNull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class DebugCallbackInputStream extends InputStream {
	private InputStream in;

	public DebugCallbackInputStream(@NotNull InputStream in) {
		Objects.requireNonNull(in, "in is null");
		this.in = in;
	}

	@Override
	public int read(byte @NotNull [] b) throws IOException {
		return this.in.read(b);
	}

	@Override
	public int read(byte @NotNull [] b, int off, int len) throws IOException {
		return this.in.read(b, off, len);
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
		default void onRead(int totalRead, byte[] data) {
		}
	}
}
