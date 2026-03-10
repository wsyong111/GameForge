package io.github.wsyong11.gameforge.util.io;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class RingByteBuffer {
	private final int maxCapacity;
	private final ByteArrayList buffer;
	private int endIndex;

	public RingByteBuffer(int maxCapacity) {
		this.maxCapacity = maxCapacity;
		this.buffer = new ByteArrayList();
		this.endIndex = 0;
	}

	private void ensureSize(int index) {
		int size = this.buffer.size();
		if (size >= index)
			return;

		this.buffer.size(Math.min(index, this.maxCapacity));
	}

	public void put(byte @NotNull [] data, int offset, int length) {
		Objects.requireNonNull(data, "data is null");
		Objects.checkFromIndexSize(offset, length, data.length);

		if (length >= this.maxCapacity) {
			this.ensureSize(this.maxCapacity);
			System.arraycopy(
				data, length - this.maxCapacity,
				this.buffer.elements(), 0,
				this.maxCapacity
			);
			this.endIndex = 0;
			return;
		}

		this.ensureSize(this.endIndex + length);
		int firstPart = Math.min(length, this.maxCapacity -this.endIndex);
		System.arraycopy(
			data, offset,
			this.buffer.elements(), this.endIndex,
			firstPart
		);

		int remaining = length - firstPart;
		if (remaining > 0)
			System.arraycopy(
				data, offset + firstPart,
				this.buffer.elements(), 0,
				remaining
			);

		this.endIndex = (this.endIndex + length) % this.maxCapacity;
	}

	public byte get(int position) {
return 0;
	}

	public int get(byte @NotNull [] data, int offset, int length, int position) {

return 0;
	}

	public int size() {

return 0;
	}

	public int getMaxCapacity() {
		return this.maxCapacity;
	}

	public void clear() {

	}

	public static void main(String[] args) {
		RingByteBuffer buffer = new RingByteBuffer(32);

		byte[] d = new byte[] {0, 1, 2};
		for (int i = 0; i < 256; i++) {
			d[0] ++;
			d[1] ++;
			d[2] ++;
			buffer.put(d, 0, 3);
			System.out.println(Arrays.toString(buffer.buffer.elements()));
		}
	}
}
