package io.github.wsyong11.gameforge.framework.system.audio.audio.pcm;

import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;
import java.util.Objects;

public abstract class AbstractPCMArray implements PCMArray {
	protected int offset(int channel, int frame) {
		return (frame * this.getChannels()) + channel;
	}

	protected void checkBoundsChannel(int channel) {
		if (channel < 0 || channel >= this.getChannels())
			throw new IndexOutOfBoundsException("Channel out of range: " + channel);
	}

	protected void checkBoundsFrame(int frame) {
		if (frame < 0 || frame >= this.getFrames())
			throw new IndexOutOfBoundsException("Frame out of range: " + frame);
	}

	@Override
	public int getFrame(float @NotNull [] array, int offset, int length, int frame) {
		Objects.requireNonNull(array, "array is null");
		Objects.checkFromIndexSize(offset, length, frame);
		this.checkBoundsFrame(frame);

		int channels = this.getChannels();
		int available = length / channels;

		for (int i = 0; i < available; i++) {
			int frameSamples = i * channels;

			for (int c = 0; c < channels; c++)
				array[offset + frameSamples + c] = this.getSample(c, frame + i);
		}

		return available;
	}

	@Override
	public int getFrame(@NotNull FloatBuffer dst, int frame, int frameCount) {
		Objects.requireNonNull(dst, "dst is null");
		this.checkBoundsFrame(frame);

		int channels = this.getChannels();
		int available = dst.remaining() / channels;

		for (int i = 0; i < available; i++)
			for (int c = 0; c < channels; c++)
				dst.put(this.getSample(c, frame + i));

		return available;
	}

	@Override
	public int setFrame(float @NotNull [] array, int offset, int length, int frame) {
		Objects.requireNonNull(array, "array is null");
		Objects.checkFromIndexSize(offset, length, frame);
		this.checkBoundsFrame(frame);

		int channels = this.getChannels();
		int available = length / channels;

		for (int i = 0; i < available; i++) {
			int frameSamples = i * channels;

			for (int c = 0; c < channels; c++)
				this.setSample(c, frame + i, array[offset + frameSamples + c]);
		}

		return available;
	}

	@Override
	public int setFrame(@NotNull FloatBuffer src, int frame, int frameCount) {
		Objects.requireNonNull(src, "src is null");
		this.checkBoundsFrame(frame);

		int channels = this.getChannels();
		int available = src.remaining() / channels;

		for (int i = 0; i < available; i++)
			for (int c = 0; c < channels; c++)
				this.setSample(c, frame + i, src.get());

		return available;
	}
}
