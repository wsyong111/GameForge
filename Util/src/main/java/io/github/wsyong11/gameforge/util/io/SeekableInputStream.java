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

	private final ByteArrayList buffer;
	private final int maxCapacity;

	private long position;
	private boolean eof;

	private final byte[] tempBuf;

	public SeekableInputStream(@NotNull InputStream stream) {
		this(stream, Integer.MAX_VALUE);
	}

	public SeekableInputStream(@NotNull InputStream stream, int maxCapacity) {
		super(Objects.requireNonNull(stream, "stream is null"));

		this.buffer = new ByteArrayList();
		this.maxCapacity = maxCapacity;

		this.position = 0L;
		this.eof = false;

		this.tempBuf = new byte[BUFFER_SIZE];
	}

	private void ensureOpen() throws IOException {
		if (this.in == null)
			throw new IOException("Stream closed");
	}

	private void ensureAvailable(long pos) throws IOException {
		this.ensureOpen();

		if (pos <= this.buffer.size() || this.eof)
			return;

		long available = pos - this.buffer.size();
		while (available > 0L) {
			int len = this.in.read(this.tempBuf, 0, (int) Math.min(BUFFER_SIZE, available));
			if (len == -1) {
				this.eof = true;
				break;
			}

			long newSize = this.buffer.size() + len;
			if (newSize > this.maxCapacity) {
				int toRemove = (int) (newSize - this.maxCapacity);
				this.buffer.removeElements(0, toRemove); // 移除最老字节
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

		int data = this.buffer.getByte((int) this.position) & 0xFF;
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

		int available = (int) Math.min(len, this.buffer.size() - this.position);

		if (available <= 0)
			return -1;

		this.buffer.getElements((int) this.position, b, off, available);

		this.position += available;
		return available;
	}

	public void seek(long pos) throws IOException {
		if (pos < 0L)
			throw new IllegalArgumentException("The position is negative");

		this.ensureOpen();

		this.ensureAvailable(pos);
		if (pos > this.buffer.size())
			throw new EOFException();

		this.position = pos;
	}

	public void clearBuffer(){
		this.buffer.size(0);
	}

	public void trim(){
		this.buffer.trim();
	}

	public long getPosition() {
		return this.position;
	}

	public int getBufferSize() {
		return this.buffer.size();
	}

	public int getMaxCapacity() {
		return this.maxCapacity;
	}

	@Override
	public int available() throws IOException {
		int inputAvailable = this.in != null ? this.in.available() : 0;
		return this.buffer.size() - ((int) this.position) + inputAvailable;
	}

	@Override
	public synchronized void close() throws IOException {
		if (this.in == null)
			return;

		try {
			this.in.close();
		} finally {
			this.in = null;
			this.buffer.size(0);
			this.buffer.trim();
		}
	}
}
