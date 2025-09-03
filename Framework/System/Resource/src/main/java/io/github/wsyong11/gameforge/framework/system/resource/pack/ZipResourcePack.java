package io.github.wsyong11.gameforge.framework.system.resource.pack;

import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Set;

public class ZipResourcePack extends AbstractResourcePack{
	// root + id + path

	private final String id;

	public ZipResourcePack(@NotNull String id, @NotNull String rootPath) {
		this.id = id;
	}

	@NotNull
	@Override
	public  String getId() {
		return this.id;
	}

	@Override
	public @Nullable Resource getResource(@NotNull String path) {
		return null;
	}

	@Override
	public @NotNull @UnmodifiableView Set<String> listResources() {
		return Set.of();
	}
}
