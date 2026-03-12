package io.github.wsyong11.gameforge.util.io;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class SeekableInputStreamTest {

    @Test
    void testReadSingleByte() throws IOException {
        byte[] data = {1, 2, 3, 4, 5};
        try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data))) {
            for (int i = 0; i < data.length; i++) {
                assertEquals(data[i], sis.read());
                assertEquals(i + 1, sis.getPosition());
            }
            assertEquals(-1, sis.read()); // 到流末尾返回 -1
        }
    }

    @Test
    void testReadByteArray() throws IOException {
        byte[] data = {10, 20, 30, 40, 50};
        byte[] buf = new byte[3];

        try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data))) {
            int n = sis.read(buf, 0, buf.length);
            assertEquals(3, n);
            assertArrayEquals(new byte[]{10, 20, 30}, buf);
            assertEquals(3, sis.getPosition());

            n = sis.read(buf, 0, buf.length);
            assertEquals(2, n); // 剩余 2 个字节
            assertArrayEquals(new byte[]{40, 50, 30}, buf); // 注意未覆盖的最后一位仍旧是旧值
            assertEquals(5, sis.getPosition());

            n = sis.read(buf, 0, buf.length);
            assertEquals(-1, n); // 已经没有数据
        }
    }

    @Test
    void testSeekAndRead() throws IOException {
        byte[] data = {5, 10, 15, 20};
        try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data))) {
            sis.seek(2);
            assertEquals(15, sis.read());
            assertEquals(3, sis.getPosition());

            sis.seek(0);
            assertEquals(5, sis.read());
        }
    }

    @Test
    void testSeekBeyondAvailable() throws IOException {
        byte[] data = {1, 2, 3};
        try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data))) {
            assertThrows(EOFException.class, () -> sis.seek(5));
        }
    }

    @Test
    void testDuplicateStream() throws IOException {
        byte[] data = {1, 2, 3};
        try (SeekableInputStream sis1 = new SeekableInputStream(new ByteArrayInputStream(data))) {
            sis1.read(); // 读取 1

            SeekableInputStream sis2 = sis1.duplicate();
            assertEquals(1, sis2.getPosition());

            assertEquals(2, sis2.read());
            assertEquals(2, sis2.getPosition());

            assertEquals(2, sis1.read()); // 原始流位置独立
            sis2.close();
        }
    }

    @Test
    void testAvailable() throws IOException {
        byte[] data = {1, 2, 3, 4};
        try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data))) {
            assertTrue(sis.available() >= 4);
            sis.read();
            assertTrue(sis.available() >= 3);
        }
    }

    @Test
    void testCloseReleasesResources() throws IOException {
        byte[] data = {1, 2, 3};
        SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data));
        sis.close();
        assertThrows(IOException.class, sis::available);
        assertThrows(IOException.class, () -> sis.seek(0));
    }

    @Test
    void testReadAfterEOFReturnsMinusOne() throws IOException {
        byte[] data = {1};
        try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data))) {
            assertEquals(1, sis.read());
            assertEquals(-1, sis.read());
        }
    }

    @Test
    void testSeekNegativeThrows() {
        byte[] data = {1, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> {
            try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data))) {
                sis.seek(-1);
            }
        });
    }

    @Test
    void testReadByteThrowsWhenDataDropped() throws IOException {
        byte[] data = new byte[5];
        for (int i = 0; i < 5; i++) data[i] = (byte) i;

        SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data), 3); // 小 buffer 容量

        // 读完数据导致最早字节被丢弃
        for (int i = 0; i < 5; i++) {
            sis.read();
        }

        long droppedPos = 0;
        assertThrows(IOException.class, () -> sis.seek(droppedPos)); // 尝试 seek 已丢弃的数据
        sis.close();
    }

    @Test
    void testBufferWrapAround() throws IOException {
        byte[] data = new byte[10];
        for (int i = 0; i < 10; i++) data[i] = (byte) (i + 1);

        // 限制 buffer 容量小于数据长度触发环形写入
        try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data), 5)) {
            byte[] buf = new byte[5];

            int n = sis.read(buf, 0, buf.length);
            assertEquals(5, n);
            assertArrayEquals(new byte[]{1,2,3,4,5}, buf);

            n = sis.read(buf, 0, buf.length);
            assertEquals(5, n);
            assertArrayEquals(new byte[]{6,7,8,9,10}, buf);

            // 尝试读取已经被丢弃的数据
            assertThrows(IOException.class, () -> sis.seek(0));
        }
    }

    @Test
    void testMultipleDuplicatesRefCount() throws IOException {
        byte[] data = {10, 20, 30, 40};
        SeekableInputStream sis1 = new SeekableInputStream(new ByteArrayInputStream(data));

        SeekableInputStream sis2 = sis1.duplicate();
        SeekableInputStream sis3 = sis2.duplicate();

        assertEquals(0, sis1.getPosition());
        sis1.read();
        assertEquals(1, sis1.getPosition());
        assertEquals(0, sis2.getPosition());
        assertEquals(0, sis3.getPosition());

        sis2.read();
        assertEquals(1, sis2.getPosition());
        assertEquals(0, sis3.getPosition());

        sis3.read();
        assertEquals(1, sis3.getPosition());

        // 关闭原始流，refCount 仍 >0，流不关闭
        sis1.close();
        assertDoesNotThrow(() -> sis2.read());
        assertDoesNotThrow(() -> sis3.read());

        sis2.close();
        sis3.close();
    }

    @Test
    void testReadAcrossBufferBoundary() throws IOException {
        byte[] data = new byte[8];
        for (int i = 0; i < 8; i++) data[i] = (byte) (i + 1);

        try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data), 5)) {
            byte[] buf = new byte[5];
            int n = sis.read(buf, 0, buf.length);
            assertEquals(5, n);
            assertArrayEquals(new byte[]{1,2,3,4,5}, buf);
        }
    }

    @Test
    void testAvailableReflectsBufferAndStream() throws IOException {
        byte[] data = {1, 2, 3, 4};
        try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data))) {
            int avail1 = sis.available();
            assertTrue(avail1 >= 4);

            sis.read();
            int avail2 = sis.available();
            assertTrue(avail2 >= 3);
        }
    }

    @Test
    void testSeekAfterPartialRead() throws IOException {
        byte[] data = {5, 6, 7, 8};
        try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data))) {
            sis.read(); // 读取 5
            sis.seek(2); // 跳到 7
            assertEquals(7, sis.read());
        }
    }

    @Test
    void testConcurrentRead() throws InterruptedException, IOException {
        byte[] data = new byte[1000];
        for (int i = 0; i < 1000; i++) data[i] = (byte) (i % 256);

        SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data), 200);

        int threads = 5;
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicBoolean failed = new AtomicBoolean(false);

        for (int t = 0; t < threads; t++) {
            new Thread(() -> {
                try {
                    SeekableInputStream dup = sis.duplicate();
                    byte[] buf = new byte[100];
                    int readTotal = 0;
                    int n;
                    while ((n = dup.read(buf)) != -1) {
                        readTotal += n;
                    }
                    assertEquals(1000, readTotal);
                    dup.close();
                } catch (Exception e) {
                    failed.set(true);e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
        sis.close();
        assertFalse(failed.get());
    }

    @Test
    void testReadZeroLengthArrayReturnsZero() throws IOException {
        byte[] data = {1, 2, 3};
        try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data))) {
            byte[] buf = new byte[0];
            int n = sis.read(buf, 0, 0);
            assertEquals(0, n);
        }
    }

    @Test
    void testReadThrowsOnNullArray() {
        assertThrows(NullPointerException.class, () -> {
            try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(new byte[]{1}))) {
                sis.read(null, 0, 1);
            }
        });
    }

    @Test
    void testReadThrowsOnInvalidOffsetLength() {
        byte[] data = {1,2,3};
        assertThrows(IndexOutOfBoundsException.class, () -> {
            try (SeekableInputStream sis = new SeekableInputStream(new ByteArrayInputStream(data))) {
                sis.read(new byte[2], 1, 2); // 超出数组长度
            }
        });
    }
}
