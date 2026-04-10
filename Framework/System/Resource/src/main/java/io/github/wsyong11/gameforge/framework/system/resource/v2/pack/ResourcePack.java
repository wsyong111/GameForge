package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.framework.system.resource.v2.fs.ResourceFileSystem;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

public interface ResourcePack extends ResourceFileSystem, Closeable {
	@Nullable
	URI getSource();

	void load(@Nullable RefreshStatus status) throws IOException;

	class RefreshStatus {

	}
}
