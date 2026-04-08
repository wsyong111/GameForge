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
import java.util.Iterator;
import java.util.Objects;

public class AssetsResourcePack extends FlatResourcePack<AssetsResourcePack.AssetsResource> {
	private static final Logger LOGGER = Log.getLogger();

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

	@NotNull
	@Override
	protected Iterator<AssetsResource> getResourceList() throws IOException {
		Assets.ensure();
		return Assets
			.getEntries()
			.stream()
			.map(AssetsResource::new)
			.iterator();
	}

	protected class AssetsResource extends AbstractResource {
		private final AssetsEntry entry;
		private final ResourcePath path;

		public AssetsResource(@NotNull AssetsEntry entry) {
			Objects.requireNonNull(entry, "entry is null");

			this.entry = entry;
			this.path = ResourcePath.of(this.entry.getName());
		}

		@NotNull
		@Override
		public InputStream openStream() throws IOException {
			this.ensurePackOpen();

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
			if (this.isPackClosed())
				return -1L;

			return this.entry.getSize();
		}
	}
}
