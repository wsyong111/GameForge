package io.github.wsyong11.gameforge.framework.system.resource.v2.fs;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourceFileWalker;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourceWalkVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public interface ResourceFileSystem {
	@NotNull
	@Unmodifiable
	List<ResourcePath> list(@NotNull ResourcePath path) throws IOException;

	@NotNull
	Resource get(@NotNull ResourcePath path) throws IOException;

	default long getSize(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");
		return this.get(path).getSize();
	}

	boolean exist(@NotNull ResourcePath path);

	boolean isDirectory(@NotNull ResourcePath path);

	boolean isFile(@NotNull ResourcePath path);

	default void walk(@NotNull ResourcePath path, int maxDepths, @NotNull ResourceWalkVisitor visitor) throws IOException {
		ResourceFileWalker.walk(this, path, maxDepths, visitor);
	}
}
