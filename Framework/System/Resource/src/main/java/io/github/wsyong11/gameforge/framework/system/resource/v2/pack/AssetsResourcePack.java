package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.assets.Assets;
import io.github.wsyong11.gameforge.assets.AssetsEntry;
import io.github.wsyong11.gameforge.framework.ex.io.FileClosedException;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AssetsResourcePack extends AbstractResourcePack {
	private volatile Map<ResourcePath, AssetsResource> resourceMap;
	private volatile Map<ResourcePath, List<ResourcePath>> fileTree;
	private volatile boolean loaded;

	private boolean closed;

	public AssetsResourcePack() {
		this.resourceMap = null;
		this.fileTree = null;
		this.loaded = false;

		this.closed = false;
	}

	@Nullable
	@Override
	public URI getSource() {
		try {
			URL resource = Assets.class.getClassLoader().getResource("");
			if (resource == null)
				return null;

			return resource.toURI();
		} catch (URISyntaxException e) {
			return null;
		}
	}

	private void ensureAssets() throws IOException {
		this.ensureOpen();

		if (this.loaded)
			return;

		synchronized (this) {
			if (this.loaded)
				return;

			try {
				Assets.ensure();
			} catch (IOException e) {
				throw new IOException("Failed get JAR resource list", e);
			}

			List<AssetsEntry> entries = Assets.getEntries();

			this.resourceMap = entries
				.stream()
				.map(AssetsResource::new)
				.collect(Collectors.toUnmodifiableMap(
					Resource::getPath,
					Function.identity()
				));

			Map<ResourcePath, Set<ResourcePath>> tree = new HashMap<>();
			for (ResourcePath file : this.resourceMap.keySet()) {
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

			this.loaded = true;
		}
	}

	@Override
	public void refresh() throws IOException {
		this.ensureOpen();

		this.loaded = false;
		this.ensureAssets();
	}

	@NotNull
	@Override
	public List<ResourcePath> list(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		this.ensureOpen();
		this.ensureAssets();

		List<ResourcePath> children = this.fileTree.get(path.toDirectory());
		if (children == null)
			throw new FileNotFoundException(path.toString());

		return children;
	}

	@NotNull
	@Override
	public Resource get(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		this.ensureOpen();
		this.ensureAssets();

		AssetsResource resource = this.resourceMap.get(path);
		if (resource == null)
			throw new FileNotFoundException(path.toString());

		return resource;
	}

	@Override
	public boolean exist(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (this.closed)
			return false;

		if (path.isRoot())
			return true; // 根目录一定存在

		try {
			this.ensureAssets();
		} catch (IOException e) {
			return false;
		}

		return this.resourceMap.containsKey(path.toFile())
			|| this.fileTree.containsKey(path.toDirectory());
	}

	@Override
	public boolean isDirectory(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (this.closed)
			return false;

		try {
			this.ensureAssets();
		} catch (IOException e) {
			return false;
		}

		return this.fileTree.containsKey(path.toDirectory());
	}

	@Override
	public boolean isFile(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (this.closed)
			return false;

		try {
			this.ensureAssets();
		} catch (IOException e) {
			return false;
		}

		return this.resourceMap.containsKey(path.toFile());
	}

	@Override
	public void close() throws IOException {
		if (this.closed)
			return;
		this.closed = true;

		for (AssetsResource resource : this.resourceMap.values())
			resource.close();

		this.fileTree = null;
		this.resourceMap = null;
	}

	private static class AssetsResource implements Resource {
		private final AssetsEntry entry;
		private final ResourcePath path;
		private volatile boolean closed;

		public AssetsResource(@NotNull AssetsEntry entry) {
			Objects.requireNonNull(entry, "entry is null");

			this.entry = entry;
			this.path = ResourcePath.of(this.entry.getName());

			this.closed = false;
		}

		private void ensureOpen() throws IOException {
			if (this.closed)
				throw new FileClosedException("Resource pack closed");
		}

		public void close() {
			this.closed = true;
		}

		@NotNull
		public AssetsEntry getEntry() {
			return this.entry;
		}

		@NotNull
		@Override
		public InputStream openStream() throws IOException {
			this.ensureOpen();

			InputStream stream = this.entry.openStream();
			if (stream == null)
				throw new FileNotFoundException(this.entry.getName());

			return stream;
		}

		@NotNull
		@Override
		public ResourcePath getPath() {
			return this.path;
		}

		@Override
		public long getSize() {
			if (this.closed)
				return -1L;

			return this.entry.getSize();
		}
	}
}
