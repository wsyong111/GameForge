package io.github.wsyong11.gameforge.framework.system.resource.pack;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.ex.FileClosedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResourcePack implements ResourcePack, Closeable {
	private final String id;
	private final Path filePath;

	private final Map<ResourcePath, ZipResource> entryMap;
	private boolean closed;
	private ZipFile file;

	public ZipResourcePack(@NotNull Path zipPath, @NotNull String id) throws IOException {
		Objects.requireNonNull(zipPath, "zipPath is null");
		Objects.requireNonNull(id, "id is null");

		this.id = id;
		this.filePath = zipPath;

		this.entryMap = new ConcurrentHashMap<>();
		this.closed = false;
		this.file = new ZipFile(zipPath.toFile());
	}

	@Override
	public boolean isClosed() {
		return this.closed;
	}

	@NotNull
	@Override
	public String getId() {
		return this.id;
	}

	@Nullable
	@Override
	public Resource getResource(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (this.closed)
			return null;

		return this.entryMap.computeIfAbsent(path, this::findResource);
	}

	@Nullable
	private ZipResource findResource(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (this.closed)
			throw new IllegalStateException("Cannot find resource because the zip file is closed");

		if (!Identifier.isValidPath(path.toString()))
			return null;

		ZipEntry entry = this.file.getEntry(path.toString());
		if (entry == null || entry.isDirectory())
			return null;

		return new ZipResource(path, this.filePath, this.file, entry);
	}

	@NotNull
	@Unmodifiable
	@Override
	public Set<ResourcePath> listResources() {
		return this.closed ? Set.of() : this.file
			.stream()
			.filter(entry -> !entry.isDirectory())
			.map(ZipEntry::getName)
			.filter(Identifier::isValidPath)
			.map(ResourcePath::of)
			.collect(Collectors.toSet());
	}

	@Override
	public void close() throws IOException {
		if (this.closed)
			return;
		this.closed = true;

		try {
			for (ZipResource resource : this.entryMap.values())
				resource.close();

			this.file.close();
		} finally {
			this.entryMap.clear();
			this.file = null;
		}
	}

	@Override
	public String toString() {
		return "ZipResourcePack[\"" + this.filePath + "\", \"" + this.id + "\"]";
	}

	private static class ZipResource implements Resource {
		private final ResourcePath path;
		private final Path filePath;

		private ZipFile file;
		private ZipEntry entry;

		private boolean closed;

		public ZipResource(@NotNull ResourcePath path, @NotNull Path filePath, @NotNull ZipFile file, @NotNull ZipEntry entry) {
			Objects.requireNonNull(path, "path is null");
			Objects.requireNonNull(filePath, "filePath is null");
			Objects.requireNonNull(file, "file is null");
			Objects.requireNonNull(entry, "entry is null");

			this.path = path;
			this.filePath = filePath;

			this.file = file;
			this.entry = entry;
		}

		public void close() {
			this.closed = true;
			this.file = null;
			this.entry = null;
		}

		@NotNull
		@Override
		public ResourcePath getPath() {
			return this.path;
		}

		@NotNull
		@Override
		public InputStream openStream() throws IOException {
			if (this.closed)
				throw new FileClosedException("Cannot open stream from the closed zip file");

			return this.file.getInputStream(this.entry);
		}

		@Override
		public long size() {
			return this.closed ? -1L : this.entry.getSize();
		}

		@Override
		public String toString() {
			return "ZipResource[\"" + this.filePath + "!" + ResourcePath.separator + this.path + "\"]";
		}
	}
}
