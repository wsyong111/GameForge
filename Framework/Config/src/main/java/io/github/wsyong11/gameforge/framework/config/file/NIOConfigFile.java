package io.github.wsyong11.gameforge.framework.config.file;

import io.github.wsyong11.gameforge.framework.config.file.listener.ConfigFileChangeListener;
import io.github.wsyong11.gameforge.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class NIOConfigFile implements ConfigFile, Closeable {
	private final FileChannel channel;

	private boolean closed;

	public NIOConfigFile(@NotNull Path path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		this.channel = FileChannel.open(path,
			StandardOpenOption.READ,
			StandardOpenOption.WRITE,
			StandardOpenOption.CREATE);

		this.closed = false;
	}

	@Override
	public @NotNull InputStream read() throws IOException {
		return null;
	}

	@Override
	public @NotNull OutputStream write() throws IOException {
		return null;
	}

	@Override
	public void reload() {

	}

	@Override
	public boolean isAvailable() {
		return false;
	}

	@Override
	public void addChangeListener(@NotNull ConfigFileChangeListener listener) {

	}

	@Override
	public void removeChangeListener(@NotNull ConfigFileChangeListener listener) {

	}

	@Override
	public synchronized void close() throws IOException {
		if (this.closed)
			return;

		this.channel.close();
	}
}
