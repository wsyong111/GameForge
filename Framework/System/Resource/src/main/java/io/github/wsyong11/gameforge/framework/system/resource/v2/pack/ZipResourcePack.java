package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.framework.ex.io.FileClosedException;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipFile;

public class ZipResourcePack implements ResourcePack, Closeable {
	private static final Logger LOGGER = Log.getLogger();

	private final Path path;
	private volatile ZipFile file;

	private volatile boolean closed;

	public ZipResourcePack(@NotNull Path path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		this.path = path;

		this.file = null;
		this.loadFile();
	}

	private void ensureOpen() throws IOException {
		if (this.closed)
			throw new FileClosedException();
	}

	private synchronized void loadFile() throws IOException {
		if (this.file != null)
			this.closeFile();

		this.file = new ZipFile(this.path.toFile());
	}

	private void closeFile() {
		try {
			this.file.close();
		} catch (IOException e) {
			LOGGER.warn("Failed to close the zip file", e);
		}
		this.file = null;
	}

	@Nullable
	@Override
	public URI getSource() {
		return this.path.toUri();
	}

	@Override
	public void refresh() throws IOException {
		this.ensureOpen();
		this.loadFile();
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<ResourcePath> list(@NotNull ResourcePath path) throws IOException {
		this.ensureOpen();

		return List.of();
	}

	@Override
	public @NotNull Resource get(@NotNull ResourcePath path) throws IOException {
		this.ensureOpen();
		return null;
	}

	@Override
	public boolean exist(@NotNull ResourcePath path) {
		return false;
	}

	@Override
	public boolean isDirectory(@NotNull ResourcePath path) {
		return false;
	}

	@Override
	public boolean isFile(@NotNull ResourcePath path) {
		return false;
	}

	@Override
	public void close() throws IOException {
		if (this.closed)
			return;
		this.closed = true;

		this.closeFile();
	}
}
