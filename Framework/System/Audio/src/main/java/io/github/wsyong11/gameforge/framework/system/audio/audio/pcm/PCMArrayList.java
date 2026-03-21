package io.github.wsyong11.gameforge.framework.system.audio.audio.pcm;

import it.unimi.dsi.fastutil.floats.FloatArrays;
import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Objects;

// TODO: 2026/3/20 性能优化
public class PCMArrayList extends AbstractPCMArray implements PCMList {
	private final int frameRate;
	private final int channels;

	private final int maxCapacity;
	private int size;
	private float[] array;

	public PCMArrayList(int frameRate, int channels) {
		this(frameRate, channels, Integer.MAX_VALUE);
	}

	public PCMArrayList(int frameRate, int channels, int maxCapacity) {
		this.frameRate = frameRate;
		this.channels = channels;
		this.maxCapacity = maxCapacity;

		this.size = 0;
		this.array = FloatArrays.EMPTY_ARRAY;
	}

	private void ensureCapacity(int capacity) {
		int newSize = Math.min(capacity, this.maxCapacity) * this.channels;
		this.array = FloatArrays.grow(this.array, newSize);
	}

	@Override
	public int getMaxCapacity() {
		return this.maxCapacity;
	}

	@Override
	public int getChannels() {
		return this.channels;
	}

	@Override
	public int getFrameRate() {
		return this.frameRate;
	}

	@Override
	public int getFrames() {
		return this.size / this.channels;
	}

	@Override
	public int getSamples() {
		return this.size;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public int add(float @NotNull [] samples, int offset, int length, int frame) {
		Objects.requireNonNull(samples, "samples is null");
		Objects.checkFromIndexSize(offset, length, samples.length);

		int currentFrames = this.getFrames();
		if (frame < 0 || frame > currentFrames)
			throw new IndexOutOfBoundsException("Frame index out of bounds: " + frame);

		if (length <= 0)
			return 0;

		int maxAvailableFrames = this.maxCapacity - currentFrames;
		int framesToAdd = Math.min(length / this.channels, maxAvailableFrames);
		if (framesToAdd <= 0)
			return 0;

		this.ensureCapacity(currentFrames + framesToAdd);

		int usableLength = framesToAdd * this.channels;
		int insertIndex = frame * this.channels;
		int tailLength = this.size - insertIndex;

		// 移动尾部数据
		if (tailLength > 0)
			System.arraycopy(
				this.array, insertIndex,
				this.array, insertIndex + usableLength,
				tailLength
			);

		// 插入新样本
		System.arraycopy(
			samples, offset,
			this.array, insertIndex,
			usableLength
		);

		this.size += usableLength;

		return framesToAdd;
	}

	@Override
	public int add(@NotNull FloatBuffer src, int length, int frame) {
		Objects.requireNonNull(src, "src is null");

		int currentFrames = this.getFrames();
		if (frame < 0 || frame > currentFrames)
			throw new IndexOutOfBoundsException("Frame index out of bounds: " + frame);

		if (length <= 0)
			return 0;

		int remainingFrame = src.remaining() / this.channels;
		if (remainingFrame == 0)
			return 0;

		int maxAvailableFrames = this.maxCapacity - currentFrames;
		int framesToAdd = Math.min(Math.min(remainingFrame, length), maxAvailableFrames);
		this.ensureCapacity(this.size + framesToAdd);

		int usableLength = framesToAdd * this.channels;
		int insertIndex = frame * this.channels;
		int tailLength = this.size - insertIndex;

		// 移动尾部数据
		if (tailLength > 0)
			System.arraycopy(
				this.array, insertIndex,
				this.array, insertIndex + usableLength,
				tailLength
			);

		// 插入新样本
		src.get(this.array, insertIndex, usableLength);
		this.size += usableLength;

		return 0;
	}

	@Override
	public int add(@NotNull PCMArray array, int offset, int length, int frame) {
		Objects.requireNonNull(array, "array is null");
		Objects.checkFromIndexSize(offset, length, array.getFrames());

		if (this.channels != array.getChannels())
			throw new IllegalArgumentException("PCM array channel no match, channel=" + this.channels + ", arrayChannel=" + array.getChannels());

		int currentFrames = this.getFrames();
		if (frame < 0 || frame > currentFrames)
			throw new IndexOutOfBoundsException("Frame index out of bounds: " + frame);

		if (length <= 0)
			return 0;

		int maxAvailableFrames = this.maxCapacity - currentFrames;
		int framesToAdd = Math.min(length, maxAvailableFrames);
		if (framesToAdd <= 0)
			return 0;

		this.ensureCapacity(currentFrames + framesToAdd);

		int usableLength = framesToAdd * this.channels;
		int insertIndex = frame * this.channels;
		int tailLength = this.size - insertIndex;

		// 移动尾部数据
		if (tailLength > 0)
			System.arraycopy(
				this.array, insertIndex,
				this.array, insertIndex + usableLength,
				tailLength
			);

		// 插入新样本
		array.getFrame(this.array, insertIndex, usableLength, offset);

		this.size += usableLength;

		return framesToAdd;
	}

	@Override
	public int remove(int frame, int length) {
		this.checkBoundsFrame(frame);

		if (length <= 0)
			return 0;

		int removeSamples = length * this.channels;
		int start = frame * this.channels;
		int tailLength = this.size - (start + removeSamples);

		if (tailLength > 0)
			System.arraycopy(this.array, start + removeSamples, this.array, start, tailLength);

		this.size -= removeSamples;
		return length;
	}

	@Override
	public void clear() {
		this.size = 0;
	}

	@Override
	public void trim() {
		this.array = FloatArrays.trim(this.array, this.size);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public float getSample(int channel, int frame) {
		this.checkBoundsChannel(channel);
		this.checkBoundsFrame(frame);

		return this.array[this.offset(channel, frame)];
	}

	@Override
	public void setSample(int channel, int frame, float value) {
		this.checkBoundsChannel(channel);
		this.checkBoundsFrame(frame);

		this.array[this.offset(channel, frame)] = value;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public float[] toArray() {
		return Arrays.copyOf(this.array, this.size);
	}

	@Override
	public float[] toArray(float[] a) {
		Objects.requireNonNull(a, "a is null");

		float[] array = a.length < this.size
			? new float[this.size]
			: a;

		System.arraycopy(this.array, 0, array, 0, this.size);
		return array;
	}
}
