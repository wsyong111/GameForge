package io.github.wsyong11.gameforge.framework.system.resource.v2.impl.fs;

import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.fs.ResourceFileSystem;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class ResourceGraphFileSystem implements ResourceFileSystem {
	private final ResourceGraph graph;

	public ResourceGraphFileSystem(@NotNull ResourceGraph graph) {
		Objects.requireNonNull(graph, "graph is null");
		this.graph = graph;
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<ResourcePath> list(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		List<ResourcePath> result = this.graph.listChildren(path);
		if (result == null)
			throw new FileNotFoundException(path.toString());

		return result;
	}

	@NotNull
	@Override
	public InputStream openStream(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		Resource resource = this.graph.get(path);
		if (resource == null)
			throw new FileNotFoundException(path.toString());

		return resource.openStream();
	}

	@Override
	public long getSize(@NotNull ResourcePath path) throws IOException {
		Objects.requireNonNull(path, "path is null");

		Resource resource = this.graph.get(path);
		if (resource == null)
			throw new FileNotFoundException(path.toString());

		long size = resource.getSize();
		if (size == -1L)
			throw new IOException("Failed to get resource size from the path " + path);

		return size;
	}

	@Override
	public boolean exist(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		return this.graph.exist(path);
	}

	@Override
	public boolean isDirectory(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		return this.graph.isDir(path);
	}

	@Override
	public boolean isFile(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		return this.graph.isEntry(path);
	}
}
