package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.assets.Assets;
import io.github.wsyong11.gameforge.assets.AssetsEntry;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class AssetsResourcePack extends AbstractResourcePack {
	private static final Logger LOGGER = Log.getLogger();

	private volatile Map<ResourcePath, AssetsEntry> assets;

	public AssetsResourcePack() {
		this.assets = Map.of();
	}

	@Nullable
	@Override
	public URI getSource() {
		URL resource = Assets.class.getClassLoader().getResource("");
		if (resource == null)
			return null;

		try {
			return resource.toURI();
		} catch (URISyntaxException e) {
			LOGGER.warn("Cannot convert url to uri: {}", resource, e);
			return null;
		}
	}

	@Override
	public void load(@NotNull LoadListener listener) throws IOException {
		Objects.requireNonNull(listener, "listener is null");

		Assets.ensure();

		List<AssetsEntry> entries = Assets.getEntries();
		listener.onStart(entries.size());

		Map<ResourcePath, AssetsEntry> map = new HashMap<>();
		for (AssetsEntry entry : entries) {
			ResourcePath path = ResourcePath.of(entry.getName()).toFile();

			map.put(path, entry);
			listener.onSuccess(path);
		}

		this.assets = Collections.unmodifiableMap(map);
		listener.onComplete();
	}

	@Override
	public boolean exist(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		return this.assets.containsKey(path.toFile());
	}

	@Override
	public long size(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		AssetsEntry entry = this.assets.get(path.toFile());
		if (entry == null)
			throw new FileNotFoundException(path.toString());

		return entry.getSize();
	}

	@NotNull
	@Override
	public InputStream open(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		AssetsEntry entry = this.assets.get(path.toFile());
		if (entry == null)
			throw new FileNotFoundException(path.toString());

		InputStream stream = entry.openStream();
		if (stream == null)
			throw new IOException("Resource isn't found in class loader " + entry.getClassLoader() + " path " + path);

		return stream;
	}

	@NotNull
	@Override
	public List<ResourcePath> list() {
		return List.copyOf(this.assets.keySet());
	}

	@Override
	public String toString() {
		return "AssetsResourcePack[\"" + this.getSource() + "\"]";
	}
}
