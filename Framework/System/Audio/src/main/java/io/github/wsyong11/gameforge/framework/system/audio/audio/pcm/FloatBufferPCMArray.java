package io.github.wsyong11.gameforge.framework.system.audio.audio.pcm;

import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;
import java.util.Objects;

public class FloatBufferPCMArray extends AbstractPCMArray {
	private final FloatBuffer buffer;
	private final int channels;
	private final int frameRate;

	public FloatBufferPCMArray(@NotNull FloatBuffer buffer, int channels, int frameRate) {
		Objects.requireNonNull(buffer, "buffer is null");

		if (channels <= 0)
			throw new IllegalArgumentException("Channel count cannot be zero or negative");

		if (frameRate <= 0)
			throw new IllegalArgumentException("Sample rate cannot be zero or negative");

		this.buffer = buffer.slice();
		this.channels = channels;
		this.frameRate = frameRate;
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
		return this.buffer.remaining() / this.channels;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public float getSample(int channel, int frame) {
		this.checkBoundsChannel(channel);
		this.checkBoundsFrame(frame);
		return this.buffer.get(this.offset(channel, frame));
	}

	@Override
	public void setSample(int channel, int frame, float value) {
		this.checkBoundsChannel(channel);
		this.checkBoundsFrame(frame);
		this.buffer.put(this.offset(channel, frame), value);
	}

	@Override
	public int getFrame(float @NotNull [] array, int offset, int length, int frame) {
		Objects.requireNonNull(array, "array is null");
		Objects.checkFromIndexSize(offset, length, array.length);
		this.checkBoundsFrame(frame);

		if (length == 0 || length < this.channels)
			return 0;

		int totalFrames = this.getFrames();
		int availableFrames = Math.min(length / this.channels, totalFrames - frame);

		int start = frame * this.channels;
		int count = availableFrames * this.channels;

		this.buffer.position(start);
		this.buffer.get(array, offset, count);
		this.buffer.position(0);

		return availableFrames;
	}

	@Override
	public int getFrame(@NotNull FloatBuffer dest, int frame, int frameCount) {
		Objects.requireNonNull(dest, "dest is null");
		this.checkBoundsFrame(frame);

		int remainingFrames = dest.remaining() / this.channels;

		int totalFrames = this.getFrames();
		int availableFrames = Math.min(frameCount, Math.min(remainingFrames, totalFrames - frame));

		int start = frame * this.channels;
		int count = availableFrames * this.channels;

		dest.put(this.buffer.slice(start, count));

		return availableFrames;
	}

	@Override
	public int setFrame(float @NotNull [] array, int offset, int length, int frame) {
		Objects.requireNonNull(array, "array is null");
		Objects.checkFromIndexSize(offset, length, array.length);
		this.checkBoundsFrame(frame);

		if (length == 0 || length < this.channels)
			return 0;

		int totalFrames = this.getFrames();
		int availableFrames = Math.min(length / this.channels, totalFrames - frame);

		int start = frame * this.channels;
		int count = availableFrames * this.channels;

		this.buffer.position(start);
		this.buffer.put(array, offset, count);
		this.buffer.position(0);

		return availableFrames;
	}

	@Override
	public int setFrame(@NotNull FloatBuffer src, int frame, int frameCount) {
		Objects.requireNonNull(src, "src is null");
		this.checkBoundsFrame(frame);

		int remainingFrames = src.remaining() / this.channels;

		int totalFrames = this.getFrames();
		int availableFrames = Math.min(frameCount, Math.min(remainingFrames, totalFrames - frame));

		int start = frame * this.channels;
		int count = availableFrames * this.channels;

		this.buffer.slice(start, count).put(src);

		return availableFrames;
	}
}
