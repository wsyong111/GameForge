package io.github.wsyong11.gameforge.framework.system.audio.audio.pcm;

import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

// TODO: 2026/3/17 Impl
public class PCMRingBuffer extends AbstractPCMArray implements PCMList {
	private static final int MAX_DEFAULT_INITIAL_SIZE = 1024 * 16; // 16KiB
	private static final int DEFAULT_LOCK_PRE_ELEMENT = 8;

	private final int capacity;
	private final int sampleRate;
	private final int channels;

	private volatile float[] buffer;
	private volatile int head;
	private volatile int tail;

	private final ReentrantLock[] locks;

//	public PCMRingBuffer(int capacity, int sampleRate, int channels) {
//		this(
//			capacity,
//			Math.min(capacity / 2, MAX_DEFAULT_INITIAL_SIZE),
//			capacity / DEFAULT_LOCK_PRE_ELEMENT,
//			sampleRate,
//			channels
//		);
//	}

	public PCMRingBuffer(int capacity, int initialCapacity, int lockCount, int sampleRate, int channels) {
		if (capacity <= 0)
			throw new IllegalArgumentException("Capacity cannot be zero or negative");

		if (initialCapacity < 0 || initialCapacity > capacity)
			throw new IllegalArgumentException("Initial capacity cannot be negative or more than capacity");

		if (lockCount <= 0 || lockCount > capacity)
			throw new IllegalArgumentException("Lock count cannot be zero, negative or more than capacity");

		if (sampleRate <= 0)
			throw new IllegalArgumentException("Sample rate cannot be zero or negative");

		if (channels <= 0)
			throw new IllegalArgumentException("Channels cannot be zero or negative");

		this.capacity = capacity;

		this.sampleRate = sampleRate;
		this.channels = channels;

		this.buffer = new float[Math.max(channels, initialCapacity)];
		this.head = 0;
		this.tail = 0;

		this.locks = new ReentrantLock[lockCount];
		for (int i = 0; i < lockCount; i++)
			this.locks[i] = new ReentrantLock();
	}

	protected int offset(int channel, int frame) {
		return (frame * this.channels) + channel;
	}

	protected int getLockIndex(int channel, int frame) {
		return this.offset(channel, frame) % this.locks.length;
	}

	@Override
	public int getMaxCapacity() {
		return this.capacity;
	}

	@Override
	public int add(float @NotNull [] samples, int offset, int length, int frame) {
		Objects.requireNonNull(samples, "samples is null");
		Objects.checkFromIndexSize(offset, length, samples.length);
		this.checkBoundsFrame(frame);

		return 0;
	}

	@Override
	public int add(@NotNull FloatBuffer src, int length, int frame) {
		return 0;
	}

	@Override
	public int add(@NotNull PCMArray array, int offset, int length, int frame) {
		Objects.requireNonNull(array, "array is null");
		Objects.checkFromIndexSize(offset, length, array.getFrames());
		this.checkBoundsFrame(frame);

		return 0;
	}

	@Override
	public int remove(int frame, int length) {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public int getChannels() {
		return this.channels;
	}

	@Override
	public int getFrameRate() {
		return this.sampleRate;
	}

	@Override
	public int getFrames() {
		return 0;
	}

	@Override
	public float getSample(int channel, int frame) {
		this.checkBoundsChannel(channel);
		this.checkBoundsFrame(frame);

		return 0;
	}

	@Override
	public void setSample(int channel, int frame, float value) {
		this.checkBoundsChannel(channel);
		this.checkBoundsFrame(frame);

	}
}
