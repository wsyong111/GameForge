package io.github.wsyong11.gameforge.framework.system.resource.v2;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ByteArrayResource implements Resource {
	private final byte[] data;
	private final ResourcePath path;

	public ByteArrayResource(byte @NotNull [] data, @NotNull ResourcePath path) {
		Objects.requireNonNull(data, "data is null");
		Objects.requireNonNull(path, "path is null");

		this.data = data;
		this.path = path;
	}

	@Override
	public @NotNull InputStream openStream() throws IOException {
		return new ByteArrayInputStream(this.data);
	}

	@NotNull
	@Override
	public ResourcePath getPath() {
		return this.path;
	}

	@Override
	public long getSize() {
		return this.data.length;
	}
}
