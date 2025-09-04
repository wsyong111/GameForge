package io.github.wsyong11.gameforge.framework.system.resource.pack;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.ex.FileClosedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DirResourcePack implements ResourcePack {
	private static final Logger LOGGER = Log.getLogger();

	private final Path dir;
	private final String id;

	private final Map<ResourcePath, FileResource> entryMap;
	private boolean closed;

	public DirResourcePack(@NotNull Path dir, @NotNull String id) throws IOException {
		Objects.requireNonNull(dir, "dir is null");
		Objects.requireNonNull(id, "id is null");

		if (!Files.isDirectory(dir))
			throw new NotDirectoryException(dir.toString());

		this.dir = dir.normalize().toAbsolutePath();
		this.id = id;

		this.entryMap = new ConcurrentHashMap<>();
		this.closed = false;
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
	private FileResource findResource(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (this.closed)
			throw new IllegalStateException("Cannot find resource because this resource pack is closed");

		Path resourcePath = this.dir.resolve(path.toString());
		if (!Files.isRegularFile(resourcePath))
			return null;

		return new FileResource(path, resourcePath);
	}

	@NotNull
	@Unmodifiable
	@Override
	public Set<ResourcePath> listResources() {
		if (this.closed)
			return Set.of();

		ResourceDirVisitor visitor = new ResourceDirVisitor(this.dir);

		try {
			Files.walkFileTree(this.dir, visitor);
		} catch (IOException e) {
			LOGGER.error("Failed to list directory from in the path {}", this.dir);
		}

		return visitor.getResult();
	}

	@Override
	public void close() {
		if (this.closed)
			return;
		this.closed = true;

		for (FileResource resource : this.entryMap.values())
			resource.close();
		this.entryMap.clear();
	}

	@Override
	public String toString() {
		return "DirResourcePack[\"" + this.dir + "\", \"" + this.id + "\"]";
	}

	private static class FileResource implements Resource {
		private final ResourcePath resourcePath;
		private final Path path;

		private boolean closed;

		private FileResource(@NotNull ResourcePath resourcePath, @NotNull Path path) {
			Objects.requireNonNull(resourcePath, "resourcePath is null");
			Objects.requireNonNull(path, "path is null");

			this.resourcePath = resourcePath;
			this.path = path;

			this.closed = false;
		}

		public void close() {
			this.closed = true;
		}

		@NotNull
		@Override
		public ResourcePath getPath() {
			return this.resourcePath;
		}

		@NotNull
		@Override
		public InputStream openStream() throws IOException {
			if (this.closed)
				throw new FileClosedException("Cannot open stream from the closed resource");

			return Files.newInputStream(this.path);
		}

		@Override
		public long size() {
			if (this.closed)
				return -1L;

			try {
				return Files.size(this.path);
			} catch (IOException e) {
				LOGGER.warn("Cannot get resource size from the path {}", this.path);
				return -1L;
			}
		}

		@Override
		public String toString() {
			return "FileResource[\"" + this.path + "\"]";
		}
	}

	private static class ResourceDirVisitor implements FileVisitor<Path> {
		private final Path rootPath;
		private final Set<ResourcePath> resourcePaths;

		private ResourceDirVisitor(@NotNull Path rootPath) {
			Objects.requireNonNull(rootPath, "rootPath is null");

			this.rootPath = rootPath.normalize().toAbsolutePath();
			this.resourcePaths = new HashSet<>();
		}

		@NotNull
		@Override
		public FileVisitResult preVisitDirectory(@NotNull Path dir, @NotNull BasicFileAttributes attrs) {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) throws IOException {
			Objects.requireNonNull(file, "file is null");

			String path = this.rootPath.relativize(file).toString().replace('\\', '/');
			if (!Identifier.isValidPath(path))
				return FileVisitResult.CONTINUE;

			this.resourcePaths.add(ResourcePath.of(path));
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(@NotNull Path file, @NotNull IOException exc) {
			Objects.requireNonNull(file, "file is null");
			Objects.requireNonNull(exc, "exc is null");

			LOGGER.warn("Visit file failed {}", file, exc);
			return FileVisitResult.CONTINUE;
		}

		@NotNull
		@Override
		public FileVisitResult postVisitDirectory(@NotNull Path dir, @Nullable IOException exc) {
			Objects.requireNonNull(dir, "dir is null");

			if (exc != null)
				LOGGER.warn("Visit directory failed {}", dir, exc);
			return FileVisitResult.CONTINUE;
		}

		@NotNull
		@UnmodifiableView
		public Set<ResourcePath> getResult() {
			return Collections.unmodifiableSet(this.resourcePaths);
		}
	}
}
