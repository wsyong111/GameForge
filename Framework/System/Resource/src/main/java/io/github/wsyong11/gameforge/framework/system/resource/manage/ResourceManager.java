package io.github.wsyong11.gameforge.framework.system.resource.manage;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceConflictHandler;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import io.github.wsyong11.gameforge.framework.system.resource.pack.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.List;

public interface ResourceManager extends IResourceManager, ResourceProvider, Closeable {
	void reload();

	void sortResourcePacks(@NotNull List<String> ids);

	void addPack(@NotNull ResourcePack pack);

	void removePack(@NotNull ResourcePack pack);

	void setConflictHandler(@NotNull Identifier id, @Nullable ResourceConflictHandler handler);

	@Override
	void close();
}
