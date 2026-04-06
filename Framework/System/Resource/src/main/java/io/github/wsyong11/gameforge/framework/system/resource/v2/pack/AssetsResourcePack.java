package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.assets.Assets;
import io.github.wsyong11.gameforge.assets.AssetsEntry;
import io.github.wsyong11.gameforge.framework.ex.io.FileClosedException;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AssetsResourcePack implements ResourcePack {
	private volatile Map<ResourcePath, AssetsResource> resourceMap;
	private volatile boolean loaded;

	private final Map<ResourcePath, List<ResourcePath>> childCache;

	private boolean closed;

	public AssetsResourcePack() {
		this.resourceMap = null;
		this.loaded = false;

		this.childCache = new ConcurrentHashMap<>();

		this.closed = false;
	}

	private void ensureAssets() throws IOException {
		if (this.closed)
			throw new FileClosedException();

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

			this.resourceMap = Assets
				.getEntries()
				.stream()
				.map(AssetsResource::new)
				.collect(Collectors.toUnmodifiableMap(
					Resource::getPath,
					Function.identity()
				));

			this.loaded = true;
		}
	}

	@Override
	public void refresh() throws IOException {
		this.loaded = false;
		this.ensureAssets();
	}

	@NotNull
	@Unmodifiable
	private List<ResourcePath> getChildItems(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (!this.isDirectory(path))
			return List.of();

		ResourcePath directory = path.toDirectory();
		return this.childCache.computeIfAbsent(path, k -> this.resourceMap
			.keySet()
			.stream()
			.filter(r -> directory.equals(r.parent()))
			.toList());
	}

	@NotNull
	@Override
	public List<ResourcePath> list(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		this.ensureAssets();
		return this.getChildItems(path);
	}

	@NotNull
	@Override
	public Resource get(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		this.ensureAssets();
		AssetsResource resource = this.resourceMap.get(path);
		if (resource == null)
			throw new FileNotFoundException(path.toString());

		return resource;
	}

	@Override
	public long getSize(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");
		return this.get(path).getSize();
	}

	@Override
	public boolean exist(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		if (path.isEmpty())
			return true; // 根目录一定存在

		try {
			this.ensureAssets();
		} catch (IOException e) {
			return false;
		}

		if (this.resourceMap.containsKey(path.toFile()))
			return true;

		ResourcePath directory = path.toDirectory();
		if (this.childCache.containsKey(directory))
			return true;

		return this.resourceMap
			.keySet()
			.stream()
			.anyMatch(r -> directory.equals(r.parent()) || r.startsWith(directory));
	}

	@Override
	public boolean isDirectory(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		// 在 Map 里一定是文件
		return !this.resourceMap.containsKey(path.toFile());
	}

	@Override
	public boolean isFile(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		return this.resourceMap.containsKey(path.toFile());
	}

	@Override
	public void walk(@NotNull ResourcePath path, int maxDepths, @NotNull ResourceWalkVisitor visitor) throws IOException {
		Objects.requireNonNull(path, "path is null");
		Objects.requireNonNull(visitor, "visitor is null");


	}

	@Override
	public void close() throws IOException {
		if (this.closed)
			return;
		this.closed = true;


	}

	private static class AssetsResource implements Resource {
		private final AssetsEntry entry;
		private final ResourcePath path;

		public AssetsResource(@NotNull AssetsEntry entry) {
			Objects.requireNonNull(entry, "entry is null");

			this.entry = entry;
			this.path = ResourcePath.of(this.entry.getName());
		}

		@NotNull
		public AssetsEntry getEntry() {
			return this.entry;
		}

		@NotNull
		@Override
		public InputStream openStream() throws IOException {
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
			return this.entry.getSize();
		}
	}
}
