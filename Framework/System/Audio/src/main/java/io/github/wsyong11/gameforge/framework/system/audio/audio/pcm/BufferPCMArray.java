package io.github.wsyong11.gameforge.framework.system.audio.audio.pcm;

import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Spliterators;
import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;

public class BufferPCMArray implements PCMArray {
	private final FloatBuffer buffer;
	private final int channels;
	private final int sampleRate;

	public BufferPCMArray(@NotNull FloatBuffer buffer, int channels, int sampleRate) {
		Objects.requireNonNull(buffer, "buffer is null");

		if (channels <= 0)
			throw new IllegalArgumentException("Channel count cannot be zero or negative");

		if (sampleRate <= 0)
			throw new IllegalArgumentException("Sample rate cannot be zero or negative");

		this.buffer = buffer.slice();
		this.channels = channels;
		this.sampleRate = sampleRate;
	}

	@Override
	public int getChannels() {
		return this.channels;
	}

	@Override
	public int getSampleRate() {
		return this.sampleRate;
	}

	@Override
	public long getFrames() {
		return this.buffer.remaining() / this.channels;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private int offset(int channel, long frame) {
		return (int) ((frame * this.channels) + channel);
	}

	private void checkBoundsChannel(int channel) {
		if (channel < 0 || channel >= this.channels)
			throw new IndexOutOfBoundsException("Channel out of range: " + channel);
	}

	private void checkBoundsFrame(long frame) {
		if (frame < 0 || frame >= this.getFrames())
			throw new IndexOutOfBoundsException("Frame out of range: " + frame);
	}

	@Override
	public float getSample(int channel, long frame) {
		this.checkBoundsChannel(channel);
		this.checkBoundsFrame(frame);
		return this.buffer.get(this.offset(channel, frame));
	}

	@Override
	public void setSample(int channel, long frame, float value) {
		this.checkBoundsChannel(channel);
		this.checkBoundsFrame(frame);
		this.buffer.put(this.offset(channel, frame), value);
	}

	@Override
	public int getFrame(float @NotNull [] array, int index, int length, long frame) {
		Objects.requireNonNull(array, "array is null");
		Objects.checkFromIndexSize(index, length, array.length);
		this.checkBoundsFrame(frame);

		if (length == 0 || length < this.channels)
			return 0;

		long totalFrames = this.getFrames();
		int availableFrames = (int) Math.min(length / this.channels, totalFrames - frame);

		int start = (int) (frame * this.channels);
		int count = availableFrames * this.channels;

		this.buffer.position(start);
		this.buffer.get(array, index, count);
		this.buffer.position(0);

		return availableFrames;
	}

	@Override
	public int getFrame(@NotNull FloatBuffer dest, long frame, int frameCount) {
		Objects.requireNonNull(dest, "dest is null");
		this.checkBoundsFrame(frame);

		int remainingFrames = dest.remaining() / this.channels;

		long totalFrames = this.getFrames();
		int availableFrames = (int) Math.min(frameCount, Math.min(remainingFrames, totalFrames - frame));

		int start = (int) (frame * this.channels);
		int count = availableFrames * this.channels;

		dest.put(this.buffer.slice(start, count));

		return availableFrames;
	}

	@Override
	public int setFrame(float @NotNull [] array, int index, int length, long frame) {
		Objects.requireNonNull(array, "array is null");
		Objects.checkFromIndexSize(index, length, array.length);
		this.checkBoundsFrame(frame);

		if (length == 0 || length < this.channels)
			return 0;

		long totalFrames = this.getFrames();
		int availableFrames = (int) Math.min(length / this.channels, totalFrames - frame);

		int start = (int) (frame * this.channels);
		int count = availableFrames * this.channels;

		this.buffer.position(start);
		this.buffer.put(array, index, count);
		this.buffer.position(0);

		return availableFrames;
	}

	@Override
	public int setFrame(@NotNull FloatBuffer src, long frame, int frameCount) {
		Objects.requireNonNull(src, "src is null");
		this.checkBoundsFrame(frame);

		int remainingFrames = src.remaining() / this.channels;

		long totalFrames = this.getFrames();
		int availableFrames = (int) Math.min(frameCount, Math.min(remainingFrames, totalFrames - frame));

		int start = (int) (frame * this.channels);
		int count = availableFrames * this.channels;

		this.buffer.slice(start, count).put(src);

		return availableFrames;
	}

	@NotNull
	@Override
	public DoubleStream sampleStream(int channel) {
		class IteratorImpl implements PrimitiveIterator.OfDouble {

			@Override
			public double nextDouble() {
				return 0;
			}

			@Override
			public boolean hasNext() {
				return false;
			}
		}

		return StreamSupport.doubleStream(
			Spliterators.spliteratorUnknownSize(new IteratorImpl(), 0),
			false
		);
	}
}
