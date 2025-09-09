package io.github.wsyong11.gameforge.framework.system.resource.manage;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceConflictHandler;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import io.github.wsyong11.gameforge.framework.system.resource.listener.ReloadListener;
import io.github.wsyong11.gameforge.framework.system.resource.pack.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface ResourceManager extends ResourceProvider {
	void registerReloadListener(@NotNull ReloadListener listener);

	void unregisterReloadListener(@NotNull ReloadListener listener);

	void reload();

	@NotNull
	@UnmodifiableView
	List<ResourcePack> getResourcePacks();

	void sortResourcePacks(@NotNull List<String> ids);

	void addPack(int index, @NotNull ResourcePack pack);

	void removePack(@NotNull ResourcePack pack);

	void setConflictHandler(@NotNull Identifier id, @Nullable ResourceConflictHandler handler);
}
