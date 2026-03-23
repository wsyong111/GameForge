package io.github.wsyong11.gameforge.framework.system.audio.audio.pcm;

import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;
import java.util.Objects;

public abstract class AbstractPCMArray implements PCMArray {
	protected int offset(int channel, int frame) {
		return (frame * this.getChannels()) + channel;
	}

	protected void checkBoundsChannel(int channel) {
		int channels = this.getChannels();
		if (channel < 0 || channel >= channels)
			throw new IndexOutOfBoundsException("Channel out of range: " + channel + ", size=" + channels);
	}

	protected void checkBoundsFrame(int frame) {
		int frames = this.getFrames();
		if (frame < 0 || frame >= frames)
			throw new IndexOutOfBoundsException("Frame out of range: " + frame + ", size=" + frames);
	}

	@Override
	public int getFrame(float @NotNull [] array, int offset, int length, int frame) {
		Objects.requireNonNull(array, "array is null");
		Objects.checkFromIndexSize(offset, length, frame);
		this.checkBoundsFrame(frame);

		int channels = this.getChannels();
		int totalFrames = this.getFrames();

		int available = length / channels;
		int toRead = Math.min(available, totalFrames - frame);

		for (int i = 0; i < toRead; i++) {
			int frameSamples = i * channels;

			for (int c = 0; c < channels; c++)
				array[offset + frameSamples + c] = this.getSample(c, frame + i);
		}

		return toRead;
	}

	@Override
	public int getFrame(@NotNull FloatBuffer dst, int frame, int frameCount) {
		Objects.requireNonNull(dst, "dst is null");
		this.checkBoundsFrame(frame);

		int channels = this.getChannels();
		int totalFrames = this.getFrames();

		int available = Math.min(dst.remaining() / channels, frameCount);
		int toRead = Math.min(available, totalFrames - frame);

		for (int i = 0; i < toRead; i++)
			for (int c = 0; c < channels; c++)
				dst.put(this.getSample(c, frame + i));

		return toRead;
	}

	@Override
	public int setFrame(float @NotNull [] array, int offset, int length, int frame) {
		Objects.requireNonNull(array, "array is null");
		Objects.checkFromIndexSize(offset, length, frame);
		this.checkBoundsFrame(frame);

		int channels = this.getChannels();
		int totalFrames = this.getFrames();

		int available = length / channels;
		int toWrite = Math.min(available, totalFrames - frame);

		for (int i = 0; i < toWrite; i++) {
			int frameSamples = i * channels;

			for (int c = 0; c < channels; c++)
				this.setSample(c, frame + i, array[offset + frameSamples + c]);
		}

		return toWrite;
	}

	@Override
	public int setFrame(@NotNull FloatBuffer src, int frame, int frameCount) {
		Objects.requireNonNull(src, "src is null");
		this.checkBoundsFrame(frame);

		int channels = this.getChannels();
		int totalFrames = this.getFrames();

		int available = Math.min(src.remaining() / channels, frameCount);
		int toWrite = Math.min(available, totalFrames - frame);

		for (int i = 0; i < toWrite; i++)
			for (int c = 0; c < channels; c++)
				this.setSample(c, frame + i, src.get());

		return toWrite;
	}
}
