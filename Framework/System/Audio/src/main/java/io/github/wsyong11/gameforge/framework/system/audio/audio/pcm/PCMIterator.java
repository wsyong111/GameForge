package io.github.wsyong11.gameforge.framework.system.audio.audio.pcm;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator;

public class PCMIterator implements PrimitiveIterator.OfDouble {
	private final PCMArray array;
	private final int channel;

	private int frame;

	public PCMIterator(@NotNull PCMArray array, int channel) {
		Objects.requireNonNull(array, "array is null");

		if (channel < 0 || channel >= array.getChannels())
			throw new IndexOutOfBoundsException("Channel out of range: " + channel);

		this.array = array;
		this.channel = channel;

		this.frame = 0;
	}


	@Override
	public double nextDouble() {
		if (!this.hasNext())
			throw new NoSuchElementException();
		return this.array.getSample(this.channel, this.frame++);
	}

	@Override
	public boolean hasNext() {
		return this.frame < this.array.getFrames();
	}
}
