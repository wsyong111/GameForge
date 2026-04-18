package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.util.exception.ExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

// TODO: 2026/4/12 可能会有并发问题，需要观察
public class ZipResourcePack extends AbstractResourcePack implements Closeable {
	private static final Logger LOGGER = Log.getLogger();

	private final Path path;

	private volatile Map<ResourcePath, ZipEntry> assets;
	private volatile ZipFile file;

	public ZipResourcePack(@NotNull Path path) {
		Objects.requireNonNull(path, "path is null");

		this.path = path;

		this.assets = Map.of();
		this.file = null;
	}

	@Nullable
	@Override
	public URI getSource() {
		return this.path.toUri();
	}

	@Override
	public void load(@NotNull LoadListener listener) throws IOException {
		Objects.requireNonNull(listener, "listener is null");

		this.ensureOpen();

		Map<ResourcePath, ZipEntry> assets = new HashMap<>();

		ZipFile file = new ZipFile(this.path.toFile());
		try {
			listener.onStart(file.size());

			Enumeration<? extends ZipEntry> enumeration = file.entries();
			while (enumeration.hasMoreElements()) {
				ZipEntry entry = enumeration.nextElement();

				ResourcePath path = ResourcePath.of(entry.getName());
				if (path.isDirectory()) {
					LOGGER.trace("Skipped zip entry {} in zip file \"{}\"",
						lazy(entry::getName),
						lazy(() -> this.path.toAbsolutePath().normalize()));
					continue;
				}

				assets.put(path, entry);
				listener.onSuccess(path);
			}

			listener.onComplete();
		} catch (Exception e) {
			throw ExceptionHandler
				.create()
				.acceptSelf(e)
				.run(file::close)
				.toException("Cannot list entry", IOException::new);
		}

		ZipFile oldFile = this.file;
		if (oldFile != null) {
			try {
				oldFile.close();
			} catch (IOException e) {
				LOGGER.warn("Failed to close the old zip file", e);
			}
		}

		this.file = file;
		this.assets = Collections.unmodifiableMap(assets);
	}

	@Override
	public boolean exist(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (this.isClosed())
			return false;

		return this.assets.containsKey(path.toFile());
	}

	@Override
	public long size(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		this.ensureOpen();

		ZipEntry entry = this.assets.get(path.toFile());
		if (entry == null)
			throw new FileNotFoundException(path.toString());

		return entry.getSize();
	}

	@NotNull
	@Override
	public InputStream open(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		this.ensureOpen();

		Map<ResourcePath, ZipEntry> assets = this.assets;
		ZipFile file = this.file;

		ZipEntry entry = assets.get(path.toFile());
		if (entry == null)
			throw new FileNotFoundException(path.toString());

		return file.getInputStream(entry);
	}

	@NotNull
	@Override
	public List<ResourcePath> list() {
		if (this.isClosed())
			return List.of();

		return List.copyOf(this.assets.keySet());
	}

	@Override
	public void close() throws IOException {
		//noinspection TryFinallyCanBeTryWithResources
		try {
			super.close();
		} finally {
			ZipFile file = this.file;
			this.file = null;
			if (file != null) {
				file.close();
			}

			this.assets = Map.of();
		}
	}

	@Override
	public String toString() {
		return "ZipResourcePack[\"" + this.path.toAbsolutePath().normalize() + "\"]";
	}
}
