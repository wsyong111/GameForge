package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.framework.system.resource.v2.fs.ResourceFileSystem;

import java.io.Closeable;
import java.io.IOException;

public interface ResourcePack extends ResourceFileSystem, Closeable {
	void refresh() throws IOException;
}
