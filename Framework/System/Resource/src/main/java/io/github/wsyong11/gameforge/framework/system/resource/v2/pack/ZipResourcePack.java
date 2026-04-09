package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.zip.ZipEntry;
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
	protected Iterator<Resource> getResourceList() throws IOException {
		try {
			return this.file
				.stream()
				.<Resource>map(entry -> new ZipResource(this.file, entry))
				.iterator();
		} catch (IllegalStateException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void refresh() throws IOException {
		this.loadFile();
		super.refresh();
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			this.closeFile();
		}
	}

	protected class ZipResource extends AbstractResource {
		private final ZipFile file;
		private final ZipEntry entry;
		private final ResourcePath path;

		public ZipResource(@NotNull ZipFile file, @NotNull ZipEntry entry) {
			Objects.requireNonNull(file, "file is null");
			Objects.requireNonNull(entry, "entry is null");

			this.file = file;
			this.entry = entry;

			this.path = ResourcePath.of(this.entry.getName());
		}

		@NotNull
		@Override
		public InputStream openStream() throws IOException {
			this.ensurePackOpen();
			return this.file.getInputStream(this.entry);
		}

		@NotNull
		@Override
		public ResourcePath getPath() {
			return this.path;
		}

		@Override
		public long getSize() {
			if (this.isPackClosed())
				return -1L;

			return this.entry.getSize();
		}
	}
}
