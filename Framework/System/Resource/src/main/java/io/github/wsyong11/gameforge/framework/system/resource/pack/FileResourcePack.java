package io.github.wsyong11.gameforge.framework.system.resource.pack;

import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

public class FileResourcePack implements ResourcePack, Cloneable {
	private final Path path;

	public FileResourcePack(@NotNull Path path) throws IOException {
		Objects.requireNonNull(path, "path is null");
		this.path = path;
	}

	@NotNull
	@Override
	public ResourcePackInfo getInfo() {
		return null;
	}

	@Override
	public @NotNull String getId() {
		return "";
	}

	@Override
	public @Nullable Resource getResource(@NotNull String path) {
		return null;
	}

	@NotNull
	@Override
	public Set<String> listResources() {
		return Set.of();
	}
}
