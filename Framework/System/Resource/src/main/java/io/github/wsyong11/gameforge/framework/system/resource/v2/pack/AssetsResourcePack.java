package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.assets.Assets;
import io.github.wsyong11.gameforge.assets.AssetsEntry;
import io.github.wsyong11.gameforge.framework.ex.io.FileClosedException;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ex.ResourceException;
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
		this.closed = false;
		this.childCache = new ConcurrentHashMap<>();

		this.resourceMap = null;
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

		return this.childCache.computeIfAbsent(path, k -> this.resourceMap
			.keySet()
			.stream()
			.filter(r -> path.equals(r.parent()))
			.toList());
	}

	@NotNull
	@Override
	public List<ResourcePath> list(@NotNull ResourcePath path) throws IOException {
		this.ensureAssets();
		this.childCache.computeIfAbsent(path, k -> this.assetsEntries
			.stream()
			.filter(r -> r.getPath().startsWith(path))
			.toList())
	}

	@NotNull
	@Override
	public Resource get(@NotNull ResourcePath path) throws ResourceException {
		return null;
	}

	@Override
	public long getSize(@NotNull ResourcePath path) {
		return 0;
	}

	@Override
	public boolean exist(@NotNull ResourcePath path) {
		return false;
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
		return false;
	}

	@Override
	public void walk(@NotNull PackWalkVisitor visitor) throws IOException {

	}

	@Override
	public void close() throws IOException {
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
