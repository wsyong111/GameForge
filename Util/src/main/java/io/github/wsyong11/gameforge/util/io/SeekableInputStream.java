package io.github.wsyong11.gameforge.util.io;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 可回溯的 InputStream，内部使用环形缓存，支持 seek。
 */
public class SeekableInputStream extends InputStream {
	private static final int TEMP_BUF_SIZE = 8192;

	private final SharedState state;
	private long position;

	public SeekableInputStream(@NotNull InputStream in, int maxCapacity) {
		Objects.requireNonNull(in, "InputStream is null");
		this.state = new SharedState(in, maxCapacity);
		this.state.increaseRef();
		this.position = 0;
	}

	public SeekableInputStream(@NotNull InputStream in) {
		this(in, Integer.MAX_VALUE);
	}

	protected SeekableInputStream(@NotNull SharedState state, long position) {
		this.state = state;
		this.position = position;
		this.state.increaseRef();
	}

	@Override
	public int read() throws IOException {
		int val = this.state.readByte(this.position);
		if (val != -1)
			this.position++;
		return val;
	}

	@Override
	public int read(byte @NotNull [] b, int off, int len) throws IOException {
		Objects.requireNonNull(b, "b is null");
		Objects.checkFromIndexSize(off, len, b.length);

		int n = this.state.read(this.position, b, off, len);
		if (n > 0)
			this.position += n;
		return n;
	}

	public void seek(long pos) throws IOException {
		if (pos < 0)
			throw new IllegalArgumentException("Negative position");

		this.state.ensureOpen();
		this.state.ensureAvailable(pos);
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
		return (int) this.state.availableFrom(this.position);
	}

	@NotNull
	public SeekableInputStream duplicate() {
		return new SeekableInputStream(this.state, this.position);
	}

	@Override
	public void close() throws IOException {
		this.state.decreaseRef();
	}

	protected static class SharedState {
		private final int capacity;
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
			this.buffer = new byte[0];

			this.tailSeq = 0;
			this.headSeq = 0;
			this.dropped = 0;

			this.eof = false;
			this.refCount = 0;

			this.temp = new byte[TEMP_BUF_SIZE];
		}

		public synchronized void increaseRef() {
			this.refCount++;
		}

		public synchronized void decreaseRef() throws IOException {
			if (--this.refCount <= 0)
				this.closeForce();
		}

		public void ensureOpen() throws IOException {
			if (this.in == null)
				throw new IOException("Stream closed");
		}

		private void ensureCapacity(int requiredCapacity) {
			if (requiredCapacity <= this.buffer.length)
				return;

			int newCapacity = this.buffer.length;
			while (newCapacity < requiredCapacity && newCapacity < this.capacity) {
				newCapacity = Math.max(1, Math.min(newCapacity * 2, this.capacity));
			}

			if (newCapacity == this.buffer.length)
				return;

			byte[] newBuffer = new byte[newCapacity];

			int bufferSize = getBufferSize();
			for (int i = 0; i < bufferSize; i++) {
				newBuffer[i] = this.buffer[getLocalIndex(this.headSeq + i)];
			}

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
			this.ensureAvailable(pos);

			if (pos < this.dropped)
				throw new IOException("Data has been discarded");

			if (pos > this.tailSeq)
				return -1;

			return this.buffer[this.getLocalIndex(pos)] & 0xFF;
		}

		public synchronized int read(long pos, byte[] dst, int off, int len) throws IOException {
			Objects.checkFromIndexSize(off, len, dst.length); // 检查越界

			this.ensureAvailable(pos + len);

			long available = this.tailSeq - pos;
			if (available <= 0)
				return -1; // 没有可读数据

			int toRead = (int) Math.min(len, available);

			int startIndex = getLocalIndex(pos);
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
			ensureOpen();
			int inputAvailable = this.in != null ? this.in.available() : 0;
			return this.getBufferSize() + inputAvailable;
		}

		public int getCapacity() {
			return this.capacity;
		}

		private synchronized void closeForce() throws IOException {
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

	public static void main(String[] args) throws IOException {
		// 小缓冲区 10 bytes
		byte[] data = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes(); // 26 bytes
		SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data), 10);

		byte[] buf = new byte[5];

		// 先读前 5 个字节
		sis.read(buf);
		System.out.println("Read first 5: " + new String(buf)); // ABCDE
		System.out.println("Position: " + sis.getPosition());

		// 继续读 10 个字节，让缓冲区滚动
		buf = new byte[10];
		sis.read(buf);
		System.out.println("Read next 10: " + new String(buf)); // FGHIJKLMNO
		System.out.println("Position: " + sis.getPosition());

		// 再读 10 个字节，触发缓冲区覆盖
		buf = new byte[10];
		sis.read(buf);
		System.out.println("Read next 10: " + new String(buf)); // PQRSTUVWXY
		System.out.println("Position: " + sis.getPosition());

		// 尝试 seek 到已经丢弃的数据（比如位置 0-5），应该抛 EOFException
		try {
			sis.seek(0);
		} catch (IOException e) {
			System.out.println("Expected exception on seeking dropped data: " + e);
		}

		// 可以 seek 到还在缓冲区里的数据
		sis.seek(15); // Q
		int val = sis.read();
		System.out.println("Read at position 15: " + (char) val); // Q

		sis.close();
	}
}