package io.github.wsyong11.gameforge.framework.system.resource.v2;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.mime.MimeTypeDB;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {
	@NotNull
	InputStream openStream() throws IOException;

	@NotNull
	ResourcePath getPath();

	@Nullable
	ResourcePack getSource();

	// 如果失败则返回 -1L
	long getSize();

	@NotNull
	default MimeType getMime() {
		return MimeTypeDB.get(this.getPath().getExtension());
	}
}
