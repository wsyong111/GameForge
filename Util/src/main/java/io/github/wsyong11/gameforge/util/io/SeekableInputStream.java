package io.github.wsyong11.gameforge.util.io;

import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 可回溯的 InputStream，内部使用环形缓存，支持 seek。
 */
public class SeekableInputStream extends InputStream {
	private static final int TEMP_BUF_SIZE = 8192;

	private SharedState state;
	private final boolean root;

	private long position;

	public SeekableInputStream(@NotNull InputStream in, int maxCapacity) {
		Objects.requireNonNull(in, "InputStream is null");

		this.state = new SharedState(in, maxCapacity);
		this.state.increaseRef();

		this.root = true;
		this.position = 0;
	}

	public SeekableInputStream(@NotNull InputStream in) {
		this(in, Integer.MAX_VALUE);
	}

	protected SeekableInputStream(@NotNull SharedState state, long position) {
		Objects.requireNonNull(state, "state is null");

		if (position < 0)
			throw new IllegalArgumentException("Position cannot be negative");

		this.state = state;

		this.root = false;
		this.position = position;

		this.state.increaseRef();
	}

	private void ensureOpen() {
		if (this.state == null)
			throw new IllegalStateException("This stream closed");
	}

	private void ensureOpenIO() throws IOException {
		if (this.state == null)
			throw new IOException("This stream closed");
	}

	@Override
	public int read() throws IOException {
		this.ensureOpenIO();

		int val = this.state.readByte(this.position);
		if (val != -1)
			this.position++;
		return val;
	}

	@Override
	public int read(byte @NotNull [] b, int off, int len) throws IOException {
		Objects.requireNonNull(b, "b is null");
		Objects.checkFromIndexSize(off, len, b.length);

		this.ensureOpenIO();

		if (len == 0)
			return 0;

		int n = this.state.read(this.position, b, off, len);
		if (n > 0)
			this.position += n;
		return n;
	}

	public void seek(long pos) throws IOException {
		this.ensureOpenIO();

		if (pos < 0)
			throw new IllegalArgumentException("Negative position");

		this.state.ensureOpen();
		this.state.ensureAvailable(pos + 1);
		if (!this.state.contains(pos))
			throw new EOFException();

		this.position = pos;
	}

	public long getPosition() {
		return this.position;
	}

	public int getMaxCapacity() {
		return this.state.getCapacity();
	}

	@Override
	public int available() throws IOException {
		this.ensureOpenIO();
		return (int) this.state.availableFrom(this.position);
	}

	@NotNull
	public SeekableInputStream duplicate() {
		this.ensureOpen();
		return new SeekableInputStream(this.state, this.position);
	}

	@Override
	public void close() throws IOException {
		if (this.state == null)
			return;

		try {
			this.state.decreaseRef();
		} finally {
			this.state = null;
		}
	}

	public void forceClose() throws IOException {
		if (!this.root)
			throw new IllegalStateException("This stream is not a root stream");

		try {
			this.state.closeStream();
		} finally {
			this.state = null;
		}
	}

	public void setMaxCapacity(int newCapacity) {
		this.ensureOpen();
		this.state.setCapacity(newCapacity);
	}

	protected static class SharedState {
		private int capacity;
		private volatile byte[] buffer;

		private volatile InputStream in;

		private volatile long tailSeq;    // 全局写序号
		private volatile long headSeq;    // 全局读序号
		private volatile long dropped;    // 丢弃的字节总数

		private volatile boolean eof;
		private volatile int refCount;

		private final byte[] temp;

		public SharedState(@NotNull InputStream in, int maxCapacity) {
			Objects.requireNonNull(in, "in is null");

			this.in = in;

			this.capacity = Math.max(1, maxCapacity);
			this.buffer = new byte[1];

			this.tailSeq = 0;
			this.headSeq = 0;
			this.dropped = 0;

			this.eof = false;
			this.refCount = 0;

			this.temp = new byte[TEMP_BUF_SIZE];
		}

		public synchronized void setCapacity(int newCapacity) {
			if (newCapacity < this.capacity)
				throw new IllegalArgumentException("New capacity cannot be less than current capacity, cap=" + this.capacity + ", newCap=" + newCapacity);
			this.capacity = newCapacity;
		}

		public synchronized void increaseRef() {
			if (this.in == null)
				throw new IllegalStateException("Stream closed");

			this.refCount++;
		}

		public synchronized void decreaseRef() throws IOException {
			if (this.in == null)
				return;

			if (--this.refCount <= 0)
				this.closeStream();
		}

		public void ensureOpen() throws IOException {
			if (this.in == null)
				throw new IOException("Stream closed");
		}

		private void ensureCapacity(int requiredCapacity) {
			if (requiredCapacity <= this.buffer.length)
				return;

			int newCapacity = Math.min(Math.max(this.buffer.length * 2, requiredCapacity), this.capacity);
			if (newCapacity == this.buffer.length)
				return;

			byte[] newBuffer = new byte[newCapacity];

			int bufferSize = this.getBufferSize();
			for (int i = 0; i < bufferSize; i++)
				newBuffer[i] = this.buffer[this.getLocalIndex(this.headSeq + i)];

			this.buffer = newBuffer;
			this.headSeq = 0;
			this.tailSeq = bufferSize;
		}

		private int getLocalIndex(long seq) {
			return (int) (seq % this.capacity);
		}

		private int getBufferSize() {
			return (int) (this.tailSeq - this.headSeq);
		}

		public synchronized void ensureAvailable(long pos) throws IOException {
			this.ensureOpen();

			if (pos <= this.tailSeq)
				return;

			this.ensureCapacity((int) (pos - this.dropped + 1));

			int availableToRead = (int) (pos - this.tailSeq);

			while (!this.eof && availableToRead > 0) {
				int bufferUsed = this.getBufferSize();

				int free = this.capacity - bufferUsed;

				int len = this.in.read(this.temp, 0, Math.min(availableToRead, this.temp.length));
				if (len == -1) {
					this.eof = true;
					break;
				}

				// 如果写入会溢出，丢弃最老数据
				if (len > free) {
					long overflow = len - free;
					this.headSeq += overflow;
					this.dropped += overflow;
				}

				// 写入 buffer，考虑环绕
				int firstPart = Math.min(len, this.capacity - getLocalIndex(this.tailSeq));
				System.arraycopy(this.temp, 0, this.buffer, getLocalIndex(this.tailSeq), firstPart);

				int secondPart = len - firstPart;
				if (secondPart > 0)
					System.arraycopy(this.temp, firstPart, this.buffer, 0, secondPart);

				this.tailSeq += len;
				availableToRead -= len;
			}
		}

		public synchronized int readByte(long pos) throws IOException {
			this.ensureAvailable(pos + 1);

			if (pos < this.dropped)
				throw new IOException("Data has been discarded");

			if (pos >= this.tailSeq)
				return -1;

			return this.buffer[this.getLocalIndex(pos)] & 0xFF;
		}

		public synchronized int read(long pos, byte[] dst, int off, int len) throws IOException {
			Objects.checkFromIndexSize(off, len, dst.length);

			if (len > this.capacity)
				throw new IOException("Requested length exceeds buffer capacity, length=" + len + ", capacity=" + this.capacity);

			this.ensureAvailable(pos + len);

			long available = this.tailSeq - pos;
			if (available <= 0)
				return -1;

			int toRead = (int) Math.min(len, available);

			int startIndex = this.getLocalIndex(pos);
			int firstPart = Math.min(toRead, this.capacity - startIndex);

			System.arraycopy(this.buffer, startIndex, dst, off, firstPart);

			int secondPart = toRead - firstPart;
			if (secondPart > 0)
				System.arraycopy(this.buffer, 0, dst, off + firstPart, secondPart);

			return toRead;
		}

		public synchronized boolean contains(long pos) {
			return pos >= this.dropped && pos < this.tailSeq;
		}

		public synchronized long availableFrom(long pos) throws IOException {
			this.ensureOpen();
			int inputAvailable = this.in != null ? this.in.available() : 0;
			return Math.max(0, this.getBufferSize() - (pos - this.dropped)) + inputAvailable;
		}

		public int getCapacity() {
			return this.capacity;
		}

		public synchronized void closeStream() throws IOException {
			if (this.in == null)
				return;

			try {
				this.in.close();
			} finally {
				this.in = null;
				this.buffer = null;
				this.headSeq = 0;
				this.tailSeq = 0;
				this.eof = true;
			}
		}
	}
}