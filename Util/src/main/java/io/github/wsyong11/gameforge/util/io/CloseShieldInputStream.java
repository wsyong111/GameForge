package io.github.wsyong11.gameforge.util.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class CloseShieldInputStream extends InputStream {
	private volatile InputStream in;

	public CloseShieldInputStream(@NotNull InputStream in) {
		Objects.requireNonNull(in, "in is null");
		this.in = in;
	}

	private void ensureOpen() throws IOException {
		if (this.in == null)
			throw new IOException("Stream closed");
	}

	@Override
	public int read() throws IOException {
		this.ensureOpen();
		return this.in.read();
	}

	@Override
	public int read(byte @NotNull [] b) throws IOException {
		this.ensureOpen();
		return this.in.read(b);
	}

	@Override
	public int read(byte @NotNull [] b, int off, int len) throws IOException {
		this.ensureOpen();
		return this.in.read(b, off, len);
	}

	@Override
	public byte[] readAllBytes() throws IOException {
		this.ensureOpen();
		return this.in.readAllBytes();
	}

	@Override
	public byte[] readNBytes(int len) throws IOException {
		this.ensureOpen();
		return this.in.readNBytes(len);
	}

	@Override
	public int readNBytes(byte[] b, int off, int len) throws IOException {
		this.ensureOpen();
		return this.in.readNBytes(b, off, len);
	}

	@Override
	public long transferTo(@NotNull OutputStream out) throws IOException {
		this.ensureOpen();
		return this.in.transferTo(out);
	}

	@Override
	public boolean markSupported() {
		return this.in != null && this.in.markSupported();
	}

	@Override
	public synchronized void reset() throws IOException {
		this.ensureOpen();
		this.in.reset();
	}

	@Override
	public synchronized void mark(int readLimit) {
		if (this.in != null)
			this.in.mark(readLimit);
	}

	@Override
	public int available() throws IOException {
		this.ensureOpen();
		return this.in.available();
	}

	@Override
	public long skip(long n) throws IOException {
		this.ensureOpen();
		return this.in.skip(n);
	}

	@Override
	public void skipNBytes(long n) throws IOException {
		this.ensureOpen();
		this.in.skipNBytes(n);
	}

	@Override
	public void close() throws IOException {
		this.in = null;
	}
}
