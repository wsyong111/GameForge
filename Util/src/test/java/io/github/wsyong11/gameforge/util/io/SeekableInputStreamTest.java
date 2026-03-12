package io.github.wsyong11.gameforge.util.io;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SeekableInputStreamTest {

	@Test
	void testReadSingleByte() throws IOException {
		byte[] data = {1, 2, 3, 4, 5};
		try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data), 4)) {
			for (byte b : data) {
				int read = sis.read();
				assertEquals(b, read);
			}
			assertEquals(-1, sis.read());
		}
	}

	@Test
	void testReadByteArray() throws IOException {
		byte[] data = new byte[10];
		for (int i = 0; i < 10; i++) data[i] = (byte) i;

		try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data), 5)) {
			byte[] buf = new byte[4];
			int read = sis.read(buf, 0, buf.length);
			assertEquals(4, read);
			for (int i = 0; i < 4; i++) {
				assertEquals(i, buf[i]);
			}

			read = sis.read(buf, 0, buf.length);
			assertEquals(4, read);
			for (int i = 0; i < 4; i++) {
				assertEquals(i + 4, buf[i]);
			}

			read = sis.read(buf, 0, buf.length);
			assertEquals(2, read);
			assertEquals(8, buf[0]);
			assertEquals(9, buf[1]);
		}
	}

	@Test
	void testSeekAndRead() throws IOException {
		byte[] data = {10, 20, 30, 40, 50};
		try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data), 3)) {
			sis.seek(2);
			assertEquals(30, sis.read());
			sis.seek(0);
			assertEquals(10, sis.read());
		}
	}

	@Test
	void testDuplicateStream() throws IOException {
		byte[] data = {1, 2, 3, 4};
		try (SeekableInputStream sis1 = new SeekableInputStream(new ByteArrayInputStream(data))) {
			sis1.read(); // read first byte
			SeekableInputStream sis2 = sis1.duplicate();
			assertEquals(2, sis2.read());
			assertEquals(2, sis1.read());
		}
	}

	@Test
	void testEOF() throws IOException {
		byte[] data = {1, 2};
		try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data))) {
			sis.read();
			sis.read();
			assertEquals(-1, sis.read());
			assertThrows(EOFException.class, () -> sis.seek(10));
		}
	}

	@Test
	void testBufferOverwrite() throws IOException {
		// 缓冲区大小 3，流长度 5
		byte[] data = {1, 2, 3, 4, 5};
		try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data), 3)) {
			sis.read(); // position 0 -> dropped=0
			sis.read(); // 1 -> dropped=0
			sis.read(); // 2 -> dropped=0, buffer full
			sis.read(); // 3 -> start=1, dropped=1
			sis.read(); // 4 -> start=2, dropped=2

			// 最后缓存应该只有 3,4,5
//            assertEquals(3, sis.state.readByte(2)); // position=2
//            assertEquals(4, sis.state.readByte(3)); // position=3
//            assertEquals(5, sis.state.readByte(4)); // position=4
			assertThrows(EOFException.class, () -> sis.seek(1)); // 已覆盖
		}
	}
}
