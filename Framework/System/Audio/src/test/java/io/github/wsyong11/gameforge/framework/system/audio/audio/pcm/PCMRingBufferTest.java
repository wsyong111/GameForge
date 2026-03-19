package io.github.wsyong11.gameforge.framework.system.audio.audio.pcm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PCMRingBufferTest {

	@Test
	void testAddAndRead() {
		PCMRingBuffer buf = new PCMRingBuffer(100, 2, 1, 44100, 2);

		float[] data = {1, 2, 3, 4}; // 2 frames
		int written = buf.add(data);

		assertEquals(2, written);
		assertEquals(2, buf.getFrames());

		assertEquals(1f, buf.getSample(0, 0));
		assertEquals(2f, buf.getSample(1, 0));
		assertEquals(3f, buf.getSample(0, 1));
		assertEquals(4f, buf.getSample(1, 1));
	}

	@Test
	void testAutoExpand() {
		PCMRingBuffer buf = new PCMRingBuffer(100, 1, 1, 44100, 1);

		float[] data = new float[50];
		int written = buf.add(data);

		assertEquals(50, written);
		assertEquals(50, buf.getFrames());
	}

	@Test
	void testOverflowThrows() {
		PCMRingBuffer buf = new PCMRingBuffer(10, 2, 1, 44100, 1);

		float[] data = new float[20];

		assertThrows(IllegalStateException.class, () -> buf.add(data));
	}

	@Test
	void testInsertMiddle() {
		PCMRingBuffer buf = new PCMRingBuffer(100, 4, 1, 44100, 1);

		buf.add(new float[]{1, 2, 3, 4}); // 4 frames

		buf.add(new float[]{9, 9}, 2); // insert at frame 2

		assertEquals(6, buf.getFrames());
		assertEquals(9f, buf.getSample(0, 2));
		assertEquals(9f, buf.getSample(0, 3));
	}

	@Test
	void testRemove() {
		PCMRingBuffer buf = new PCMRingBuffer(100, 4, 1, 44100, 1);

		buf.add(new float[]{1, 2, 3, 4, 5});

		buf.remove(1, 2); // remove [2,3]

		assertEquals(3, buf.getFrames());
		assertEquals(1f, buf.getSample(0, 0));
		assertEquals(4f, buf.getSample(0, 1));
		assertEquals(5f, buf.getSample(0, 2));
	}

	@Test
	void testClear() {
		PCMRingBuffer buf = new PCMRingBuffer(100, 4, 1, 44100, 1);

		buf.add(new float[]{1, 2, 3});
		buf.clear();

		assertEquals(0, buf.getFrames());
	}

	@Test
	void testEdgeInvalidFrame() {
		PCMRingBuffer buf = new PCMRingBuffer(10, 2, 1, 44100, 1);

		assertThrows(IndexOutOfBoundsException.class,
			() -> buf.getSample(0, 0));
	}
}
