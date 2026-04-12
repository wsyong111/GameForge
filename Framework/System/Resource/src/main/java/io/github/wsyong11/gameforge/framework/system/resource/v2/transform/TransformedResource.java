package io.github.wsyong11.gameforge.framework.system.resource.v2.transform;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public abstract class TransformedResource implements Resource {
	private final Resource resource;

	public TransformedResource(@NotNull Resource resource) {
		Objects.requireNonNull(resource, "resource is null");
		this.resource = resource;
	}

	@NotNull
	protected abstract InputStream transformStream(@NotNull InputStream stream) throws IOException;

	@NotNull
	@Override
	public InputStream openStream() throws IOException {
		InputStream stream = this.resource.openStream();
		try {
			return this.transformStream(stream);
		} catch (IOException e) {
			try {
				stream.close();
			} catch (IOException ex) {
				e.addSuppressed(ex);
			}
			throw e;
		}
	}

	@NotNull
	@Override
	public ResourcePath getPath() {
		return this.resource.getPath();
	}

	@Nullable
	@Override
	public ResourcePack getSource() {
		return this.resource.getSource();
	}

	@Override
	public long getSize() {
		return this.resource.getSize();
	}

	@NotNull
	@Override
	public MimeType getMime() {
		return this.resource.getMime();
	}

	@Override
	public String toString() {
		return "TransformedResource[" + this.resource + "]";
	}
}
