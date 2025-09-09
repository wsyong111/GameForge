package io.github.wsyong11.gameforge.framework.system.resource.pack;

import io.github.wsyong11.gameforge.assets.Assets;
import io.github.wsyong11.gameforge.assets.AssetsEntry;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AssetsResourcePack implements ResourcePack {
	private static final Logger LOGGER = Log.getLogger();

	private static final Set<ResourcePath> RESOURCE_PATHS = Assets
		.getEntries()
		.stream()
		.map(AssetsEntry::getName)
		.map(ResourcePath::of)
		.collect(Collectors.toUnmodifiableSet());

	private static final Map<ResourcePath, AssetsResource> ENTRY_MAP;

	static {
		ENTRY_MAP = Assets
			.getEntries()
			.stream()
			.map(AssetsResource::new)
			.collect(Collectors.toUnmodifiableMap(AssetsResource::getPath, Function.identity()));
	}

	private final String id;

	private boolean closed;

	public AssetsResourcePack(@NotNull String id) {
		Objects.requireNonNull(id, "id is null");

		this.id = id;

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
		return ENTRY_MAP.get(path);
	}

	@NotNull
	@Unmodifiable
	@Override
	public Set<ResourcePath> listResources() {
		return RESOURCE_PATHS;
	}

	@Override
	public void close() {
		this.closed = true;
	}

	@Override
	public String toString() {
		return "AssetsResourcePack[\"" + this.id + "\"]";
	}

	private static class AssetsResource implements Resource {
		private final AssetsEntry entry;
		private final ResourcePath path;

		public AssetsResource(@NotNull AssetsEntry entry) {
			Objects.requireNonNull(entry, "entry is null");

			this.entry = entry;
			this.path = ResourcePath.of(entry.getName());
		}

		@NotNull
		@Override
		public ResourcePath getPath() {
			return this.path;
		}

		@NotNull
		@Override
		public InputStream openStream() throws IOException {
			InputStream stream = this.entry.openStream();
			if (stream == null)
				throw new IOException("Cannot open stream from assets " + this.entry.getName());
			return stream;
		}

		@Override
		public long size() {
			return this.entry.getSize();
		}

		@Override
		public String toString() {
			return "AssetsResource[\"" + this.entry.getName() + "\"]";
		}
	}
}
