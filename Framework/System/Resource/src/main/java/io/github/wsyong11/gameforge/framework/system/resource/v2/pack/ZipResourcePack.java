package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.zip.ZipFile;

public class ZipResourcePack extends FlatResourcePack implements Closeable {
	private static final Logger LOGGER = Log.getLogger();

	private final Path path;
	private volatile ZipFile file;

	public ZipResourcePack(@NotNull Path path) {
		Objects.requireNonNull(path, "path is null");

		this.path = path;

		this.file = null;
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
		} finally {
			this.file = null;
		}
	}

	@Nullable
	@Override
	public URI getSource() {
		return this.path.toUri();
	}

	@NotNull
	@Override
	protected Iterator<ResourceElement> getResourceList() throws IOException {
		try {
			return this.file
				.stream()
				.map(entry -> ResourceElement.simple(
					ResourcePath.of(entry.getName()),
					() -> this.file.getInputStream(entry),
					entry::getSize
				))
				.iterator();
		} catch (IllegalStateException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void load(@Nullable RefreshStatus status) throws IOException {
		this.loadFile();
		super.load(status);
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			this.closeFile();
		}
	}
}
