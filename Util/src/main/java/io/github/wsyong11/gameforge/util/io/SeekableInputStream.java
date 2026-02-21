package io.github.wsyong11.gameforge.util.io;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class SeekableInputStream extends FilterInputStream {
	private static final int BUFFER_SIZE = 8192;

	private final ByteList buffer;

	private int position;
	private boolean eof;

	private final byte[] tempBuf;

	public SeekableInputStream(@NotNull InputStream stream) {
		super(Objects.requireNonNull(stream, "stream is null"));

		this.buffer = new ByteArrayList();

		this.position = 0;
		this.eof = false;

		this.tempBuf = new byte[BUFFER_SIZE];
	}

	private void ensureOpen() throws IOException {
		if (this.in == null)
			throw new IOException("Stream closed");
	}

	private void ensureAvailable(int pos) throws IOException {
		this.ensureOpen();

		if (pos <= this.buffer.size() || this.eof)
			return;

		int available = pos - this.buffer.size();
		while (available > 0) {
			int len = this.in.read(this.tempBuf, 0, Math.min(BUFFER_SIZE, available));
			if (len == -1) {
				this.eof = true;
				break;
			}

			this.buffer.addElements(this.buffer.size(), this.tempBuf, 0, len);
			available -= len;
		}
	}

	@Override
	public int read() throws IOException {
		this.ensureOpen();
		this.ensureAvailable(this.position + 1);

		if (this.position >= this.buffer.size())
			return -1;

		int data = this.buffer.getByte(this.position) & 0xFF;
		this.position++;

		return data;
	}

	@Override
	public int read(byte @NotNull [] b, int off, int len) throws IOException {
		Objects.requireNonNull(b, "b is null");
		Objects.checkFromIndexSize(off, len, b.length);

		this.ensureOpen();

		if (len == 0)
			return 0;

		this.ensureAvailable(this.position + len);

		int available = Math.min(len, this.buffer.size() - this.position);

		if (available <= 0)
			return -1;

		this.buffer.getElements(this.position, b, off, available);

		this.position += available;
		return available;
	}

	public void seek(int pos) throws IOException {
		if (pos < 0)
			throw new IllegalArgumentException("The index is negative");

		this.ensureOpen();

		this.ensureAvailable(pos);
		if (pos > this.buffer.size())
			throw new EOFException();

		this.position = pos;
	}

	public int getPosition() {
		return this.position;
	}

	@Override
	public synchronized void close() throws IOException {
		if (this.in == null)
			return;

		try {
			this.in.close();
		} finally {
			this.in = null;
			this.buffer.clear();
		}
	}
}
