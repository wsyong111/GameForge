package io.github.wsyong11.gameforge.framework.system.resource.manage;

import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import io.github.wsyong11.gameforge.framework.system.resource.listener.ReloadListener;
import io.github.wsyong11.gameforge.framework.system.resource.pack.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface IResourceManager extends ResourceProvider {
	void registerReloadListener(@NotNull ReloadListener listener);

	void unregisterReloadListener(@NotNull ReloadListener listener);

	void registerReloadCallback(@NotNull Runnable callback);

	void unregisterReloadCallback(@NotNull Runnable callback);

	@NotNull
	@UnmodifiableView
	List<ResourcePack> getResourcePacks();
}
