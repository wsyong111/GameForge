package io.github.wsyong11.gameforge.framework.system.resource;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.mime.MimeTypeDB;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {
	@NotNull
	ResourcePath getPath();

	@NotNull
	InputStream openStream() throws IOException;

	long size();

	@NotNull
	default MimeType getType() {
		return MimeTypeDB.get(this.getPath().getExtension());
	}
}
