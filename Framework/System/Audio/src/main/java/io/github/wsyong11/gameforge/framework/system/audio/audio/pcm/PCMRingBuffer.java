package io.github.wsyong11.gameforge.framework.system.audio.audio.pcm;

// TODO: 2026/3/17 Impl
public class PCMRingBuffer implements PCMList {
	private final int capacity;
	private float[] buffer;

	public PCMRingBuffer(int capacity) {
		this.capacity = capacity;
	}


}
