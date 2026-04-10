package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import com.google.common.collect.Streams;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.util.exception.ExceptionSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

public abstract class FlatResourcePack extends AbstractResourcePack {
	private volatile Map<ResourcePath, ResourceElement> resourceMap;
	private volatile Map<ResourcePath, List<ResourcePath>> fileTree;
	private volatile boolean loaded;

	public FlatResourcePack() {
		this.resourceMap = null;
		this.fileTree = null;
		this.loaded = false;
	}

	@NotNull
	protected abstract Iterator<ResourceElement> getResourceList() throws IOException;

	private void ensureLoaded() throws IOException {
		if (!this.loaded)
			throw new IOException("Resource pack is not load");
	}

	@Override
	public synchronized void load(@Nullable RefreshStatus status) throws IOException {
		this.ensureOpen();

		Map<ResourcePath, ResourceElement> resourceMap = Streams
			.stream(this.getResourceList())
			.collect(Collectors.toUnmodifiableMap(
				ResourceElement::getPath,
				Function.identity()
			));

		Map<ResourcePath, Set<ResourcePath>> tree = new HashMap<>();
		for (ResourcePath file : resourceMap.keySet()) {
			ResourcePath current = file;

			while (!current.isEmpty()) {
				ResourcePath parent = current.parent();
				tree.computeIfAbsent(parent, k -> new HashSet<>())
				    .add(current);

				current = parent;
			}
		}

		tree.computeIfAbsent(ResourcePath.ROOT, k -> new HashSet<>());

		this.fileTree = tree
			.entrySet()
			.stream()
			.collect(Collectors.toUnmodifiableMap(
				Map.Entry::getKey,
				e -> List.copyOf(e.getValue())
			));
		this.resourceMap = resourceMap;

		this.loaded = true;
	}

	@NotNull
	@Override
	public List<ResourcePath> list(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		this.ensureOpen();
		this.ensureLoaded();

		List<ResourcePath> children = this.fileTree.get(path.toDirectory());
		if (children == null)
			throw new FileNotFoundException(path.toString());

		return children;
	}

	@NotNull
	protected ResourceElement getElement(@NotNull ResourcePath path) throws IOException {
		this.ensureOpen();
		this.ensureLoaded();

		ResourceElement element = this.resourceMap.get(path);
		if (element == null)
			throw new FileNotFoundException(path.toString());

		return element;
	}

	@NotNull
	@Override
	public InputStream openStream(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		ResourceElement element = this.getElement(path);
		return element.createStream();
	}

	@Override
	public long getSize(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		ResourceElement element = this.getElement(path);
		return element.getSize();
	}

	@Override
	public boolean exist(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (this.isClosed())
			return false;

		if (this.loaded)
			return false;

		if (path.isRoot())
			return true; // 根目录一定存在

		return this.resourceMap.containsKey(path.toFile())
			|| this.fileTree.containsKey(path.toDirectory());
	}

	@Override
	public boolean isDirectory(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (this.isClosed())
			return false;

		if (!this.loaded)
			return false;

		return this.fileTree.containsKey(path.toDirectory());
	}

	@Override
	public boolean isFile(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (this.isClosed())
			return false;

		if (!this.loaded)
			return false;

		return this.resourceMap.containsKey(path.toFile());
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			this.fileTree = null;
			this.resourceMap = null;
			this.loaded = false;
		}
	}

	protected interface ResourceElement {
		@NotNull
		static ResourceElement simple(
			@NotNull ResourcePath path,
			@NotNull ExceptionSupplier<InputStream, IOException> streamFactory,
			long size
		) {
			Objects.requireNonNull(path, "path is null");
			Objects.requireNonNull(streamFactory, "streamFactory is null");

			return new SimpleResourceElement(path, streamFactory, () -> size);
		}

		@NotNull
		static ResourceElement simple(
			@NotNull ResourcePath path,
			@NotNull ExceptionSupplier<InputStream, IOException> streamFactory,
			@NotNull LongSupplier sizeGetter
		) {
			Objects.requireNonNull(path, "path is null");
			Objects.requireNonNull(streamFactory, "streamFactory is null");
			Objects.requireNonNull(sizeGetter, "sizeGetter is null");

			return new SimpleResourceElement(path, streamFactory, sizeGetter);
		}

		@NotNull
		ResourcePath getPath();

		@NotNull
		InputStream createStream() throws IOException;

		long getSize() throws IOException;
	}

	protected static class SimpleResourceElement implements ResourceElement {
		private final ResourcePath path;
		private final ExceptionSupplier<InputStream, IOException> streamFactory;
		private final LongSupplier sizeGetter;

		protected SimpleResourceElement(
			@NotNull ResourcePath path,
			@NotNull ExceptionSupplier<InputStream, IOException> streamFactory,
			@NotNull LongSupplier sizeGetter
		) {
			Objects.requireNonNull(path, "path is null");
			Objects.requireNonNull(streamFactory, "streamFactory is null");

			this.path = path;
			this.streamFactory = streamFactory;
			this.sizeGetter = sizeGetter;
		}

		@NotNull
		@Override
		public ResourcePath getPath() {
			return this.path;
		}

		@NotNull
		@Override
		public InputStream createStream() throws IOException {
			return this.streamFactory.get();
		}

		@Override
		public long getSize() {
			return this.sizeGetter.getAsLong();
		}
	}
}
