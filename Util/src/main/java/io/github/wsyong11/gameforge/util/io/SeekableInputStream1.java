package io.github.wsyong11.gameforge.util.io;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

// TODO: 2026/3/8 优化 
public class SeekableInputStream1 extends InputStream {
	private static final int BUFFER_SIZE = 8192;

	private final SharedState state;

	private long position;

	public SeekableInputStream1(@NotNull InputStream stream) {
		this(stream, Integer.MAX_VALUE);
	}

	public SeekableInputStream1(@NotNull InputStream stream, int maxCapacity) {
		Objects.requireNonNull(stream, "stream is null");

		this.state = new SharedState(stream, maxCapacity);
		this.position = 0L;

		this.state.increaseRef();
	}

	protected SeekableInputStream1(@NotNull SharedState state) {
		Objects.requireNonNull(state, "state is null");

		this.state = state;
		this.position = 0L;

		this.state.increaseRef();
	}

	@NotNull
	protected SharedState getState() {
		return this.state;
	}

	@Override
	public int read() throws IOException {
		this.state.ensureOpen();
		this.state.ensureAvailable(this.position + 1);

		if (this.position >= this.state.getBufferSize())
			return -1;

		int data = this.state.getBuffer().getByte((int) this.position) & 0xFF;
		this.position++;

		return data;
	}

	@Override
	public int read(byte @NotNull [] b, int off, int len) throws IOException {
		Objects.requireNonNull(b, "b is null");
		Objects.checkFromIndexSize(off, len, b.length);

		this.state.ensureOpen();

		if (len == 0)
			return 0;

		this.state.ensureAvailable(this.position + len);

		int available = (int) Math.min(len, this.state.getBufferSize() - this.position);

		if (available <= 0)
			return -1;

		this.state.getBuffer().getElements((int) this.position, b, off, available);

		this.position += available;
		return available;
	}

	public void seek(long pos) throws IOException {
		if (pos < 0L)
			throw new IllegalArgumentException("The position is negative");

		this.state.ensureOpen();

		this.state.ensureAvailable(pos);
		if (pos > this.getBufferSize())
			throw new EOFException();

		this.position = pos;
	}

	public void clearBuffer() {
		this.state.clearBuffer();
	}

	public void trim() {
		this.state.trim();
	}

	public long getPosition() {
		return this.position;
	}

	public int getBufferSize() {
		return this.state.getBufferSize();
	}

	public int getMaxCapacity() {
		return this.state.getMaxCapacity();
	}

	@Override
	public int available() throws IOException {
		return this.state.getAvailable(this.position);
	}

	@NotNull
	public SeekableInputStream1 duplicate() {
		return new SeekableInputStream1(this.state);
	}

	@Override
	public void close() throws IOException {
		this.state.decreaseRef();
	}

	protected static class SharedState {
		private final ByteArrayList buffer;
		private final int maxCapacity;

		private volatile InputStream in;
		private volatile boolean eof;
		private final AtomicInteger refCount;

		private final byte[] tempBuf;

		public SharedState(@NotNull InputStream in, int maxCapacity) {
			Objects.requireNonNull(in, "in is null");

			this.buffer = new ByteArrayList();
			this.maxCapacity = Math.max(0, maxCapacity);

			this.in = in;
			this.eof = false;
			this.refCount = new AtomicInteger(0);

			this.tempBuf = new byte[BUFFER_SIZE];
		}

		public void increaseRef() {
			this.refCount.getAndIncrement();
		}

		public void decreaseRef() throws IOException {
			if (this.refCount.decrementAndGet() <= 0)
				this.closeForce();
		}

		public void ensureOpen() throws IOException {
			if (this.in == null)
				throw new IOException("Stream closed");
		}

		public synchronized void ensureAvailable(long pos) throws IOException {
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

		public int getBufferSize() {
			return this.buffer.size();
		}

		public int getAvailable(long position) throws IOException {
			int inputAvailable = this.in != null ? this.in.available() : 0;
			return (int) (this.buffer.size() - position + inputAvailable);
		}

		public int getMaxCapacity() {
			return this.maxCapacity;
		}

		public void clearBuffer() {
			this.buffer.clear();
		}


		public void trim() {
			this.buffer.trim();
		}

		@NotNull
		public ByteList getBuffer() {
			return this.buffer;
		}

		public synchronized void closeForce() throws IOException {
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
}
