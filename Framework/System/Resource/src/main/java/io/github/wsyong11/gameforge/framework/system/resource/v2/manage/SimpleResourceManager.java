package io.github.wsyong11.gameforge.framework.system.resource.v2.manage;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.resource.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.fs.ResourceFileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleResourceManager implements ResourceManager {
	private final List<ResourcePack> resourcePacks;
	private final Map<ResourcePack, Integer> priorityMap;

	public SimpleResourceManager() {
		this.resourcePacks = new ArrayList<>();
	}

	@Override
	public @NotNull ResourceFileSystem getFileSystem() {
		return null;
	}

	@Override
	public @NotNull ReloadStatus reload() {
		return null;
	}

	@Override
	public @NotNull @UnmodifiableView List<ResourcePack> getResourcePacks() {
		return List.of();
	}

	@Override
	public void setPackPriority(@NotNull ResourcePack pack, int priority) {

	}

	@Override
	public int getPackPriority(@NotNull ResourcePack pack) {
		return 0;
	}

	@Override
	public void registerResourcePack(@NotNull ResourcePack pack) {

	}

	@Override
	public void unregisterResourcePack(@NotNull ResourcePack pack) {

	}

	@Override
	public @Nullable Resource getResource(@NotNull Identifier location) {
		return null;
	}

	@Override
	public @Nullable @Unmodifiable List<Resource> getAllResources(@NotNull Identifier location) {
		return List.of();
	}

	@Override
	public @Nullable @Unmodifiable List<String> listResources(@NotNull Identifier location) {
		return List.of();
	}

	@Override
	public boolean hasResource(@NotNull Identifier location) {
		return false;
	}

	@Override
	public void close() throws IOException {

	}
}
