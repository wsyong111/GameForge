package io.github.wsyong11.gameforge.framework.system.resource.v2.fs;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.PackWalkVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.util.List;

public interface ResourceFileSystem {
	@NotNull
	@Unmodifiable
	List<ResourcePath> list(@NotNull ResourcePath path) throws IOException;

	@NotNull
	Resource get(@NotNull ResourcePath path) throws IOException;

	long getSize(@NotNull ResourcePath path);

	boolean exist(@NotNull ResourcePath path);

	boolean isDirectory(@NotNull ResourcePath path);

	boolean isFile(@NotNull ResourcePath path);

	void walk(@NotNull PackWalkVisitor visitor) throws IOException;
}
