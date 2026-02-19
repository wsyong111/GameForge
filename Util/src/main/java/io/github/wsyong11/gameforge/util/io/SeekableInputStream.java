package io.github.wsyong11.gameforge.util.io;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class SeekableInputStream extends InputStream {
	private static final int BUFFER_SIZE = 512;

	private final InputStream stream;
	private final ByteList buffer;

	private int currentIndex;

	public SeekableInputStream(@NotNull InputStream stream) {
		Objects.requireNonNull(stream, "stream is null");

		this.stream = stream;
		this.buffer = new ByteArrayList();

		this.currentIndex = 0;
	}

	protected void measureBuffer(int index) throws IOException {
		if (index <= this.currentIndex)
			return;

		int available = index - this.currentIndex;
		int size = Math.min(available, BUFFER_SIZE);
		byte[] buf = new byte[size];
		while (available > 0) {
			int len = this.stream.read(buf, 0, Math.min(size, available));
			if (len == -1)
				break;

			this.buffer.addElements(this.buffer.size(), buf, 0, len);
			available -= len;
		}
	}

	@Override
	public int read() throws IOException {
		return 0;
	}
}
