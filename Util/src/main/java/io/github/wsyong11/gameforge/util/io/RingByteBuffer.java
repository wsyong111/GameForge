package io.github.wsyong11.gameforge.util.io;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import org.jetbrains.annotations.NotNull;

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

	private void ensureCapacity(int index) {
		int size = this.buffer.size();
		if (size >= index)
			return;

		this.buffer.size(index);
	}

	public void put(byte @NotNull [] data, int offset, int length) {
		Objects.requireNonNull(data, "data is null");
		Objects.checkFromIndexSize(offset, length, data.length);

		if (length >= this.maxCapacity) {
			this.ensureCapacity(this.maxCapacity);
			System.arraycopy(
				data, length - this.maxCapacity,
				this.buffer.elements(), 0,
				this.maxCapacity
			);
			this.endIndex = 0;
			return;
		}

		this.ensureCapacity(Math.min(this.endIndex + length, this.maxCapacity));

		int firstPart = Math.min(length, this.maxCapacity - this.endIndex);
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

	private int calcFirstIndex(int position) {
		int size = this.buffer.size();
		return size >= this.maxCapacity
			? (this.endIndex + position + (this.maxCapacity - 1)) % this.maxCapacity
			: size - 1 - position;
	}

	private int calcLastIndex(int position) {
		int size = this.buffer.size();
		return size >= this.maxCapacity
			? (this.endIndex - position) % this.maxCapacity
			: position;
	}

	public byte getFirst(int position) {
		Objects.checkIndex(position, this.buffer.size());

		int index = this.calcFirstIndex(position);
		return this.buffer.getByte(index);
	}

	public byte getLast(int position) {
		Objects.checkIndex(position, this.buffer.size());

		int index = this.calcLastIndex(position);
		return this.buffer.getByte(index);
	}

	public int getFirst(byte @NotNull [] data, int offset, int length, int position) {
		Objects.requireNonNull(data, "data is null");
		Objects.checkFromIndexSize(offset, length, data.length);
		Objects.checkIndex(position, this.buffer.size());

		if (length == 0)
			return 0;

		int size = this.buffer.size();
		int available = size < this.maxCapacity ? size - position : this.maxCapacity;

		int index = this.calcFirstIndex(position);

		int firstPart = Math.min(available, this.maxCapacity - index);
		System.arraycopy(
			this.buffer.elements(), index,
			data, offset,
			firstPart
		);

		int remaining = available - firstPart;
		if (remaining > 0)
			System.arraycopy(
				this.buffer.elements(), 0,
				data, offset + firstPart,
				remaining
			);

		return available;
	}


	public int getLast(byte @NotNull [] data, int offset, int length, int position) {
		Objects.requireNonNull(data, "data is null");
		Objects.checkFromIndexSize(offset, length, data.length);
		Objects.checkIndex(position, this.buffer.size());

		if (length == 0)
			return 0;

		int size = this.buffer.size();
		int available = size < this.maxCapacity ? size - position : this.maxCapacity;

		int index = this.calcLastIndex(position);

		int firstPart = Math.min(available, this.maxCapacity - index);
		System.arraycopy(
			this.buffer.elements(), index,
			data, offset,
			firstPart
		);

		int remaining = available - firstPart;
		if (remaining > 0)
			System.arraycopy(
				this.buffer.elements(), 0,
				data, offset + firstPart,
				remaining
			);

		return available;
	}

	public int size() {
		return this.buffer.size();
	}

	public int getMaxCapacity() {
		return this.maxCapacity;
	}

	public void clear() {
		this.buffer.clear();
		this.buffer.trim();
		this.endIndex = 0;
	}

	public boolean isFull() {
		return this.buffer.size() >= this.maxCapacity;
	}

	public int testDropLength(int length) {
		int size = this.buffer.size();
		return size >= this.maxCapacity ? length : length - size;
	}
}
