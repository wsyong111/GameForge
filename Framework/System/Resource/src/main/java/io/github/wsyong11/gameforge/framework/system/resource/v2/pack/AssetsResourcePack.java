package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.assets.Assets;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

public class AssetsResourcePack extends FlatResourcePack {
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
	protected Iterator<ResourceElement> getResourceList() throws IOException {
		Assets.ensure();
		return Assets
			.getEntries()
			.stream()
			.map(e -> ResourceElement.simple(
				ResourcePath.of(e.getName()),
				e::openStream,
				e.getSize()
			))
			.iterator();
	}
}
