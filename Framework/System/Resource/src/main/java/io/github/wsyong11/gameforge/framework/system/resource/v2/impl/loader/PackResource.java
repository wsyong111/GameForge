package io.github.wsyong11.gameforge.framework.system.resource.v2.impl.loader;

import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class PackResource implements Resource {
	private final ResourcePack pack;
	private final ResourcePath path;

	public PackResource(@NotNull ResourcePack pack, @NotNull ResourcePath path) {
		Objects.requireNonNull(pack, "pack is null");
		Objects.requireNonNull(path, "path is null");

		this.pack = pack;
		this.path = path;
	}

	@NotNull
	@Override
	public InputStream openStream() throws IOException {
		return this.pack.open(this.path);
	}

	@NotNull
	@Override
	public ResourcePath getPath() {
		return this.path;
	}

	@NotNull
	@Override
	public ResourcePack getSource() {
		return this.pack;
	}

	@Override
	public long getSize() {
		try {
			return this.pack.size(this.path);
		} catch (IOException e) {
			return -1L;
		}
	}

	@Override
	public String toString() {
		return "Resource[\"" + StringEscapeUtils.escapeJava(this.path.toString()) + "\" in " + this.pack + "]";
	}
}
