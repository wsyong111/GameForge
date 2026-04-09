package io.github.wsyong11.gameforge.framework.system.resource.v2.manage;

import io.github.wsyong11.gameforge.framework.system.resource.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourceProvider;
import io.github.wsyong11.gameforge.framework.system.resource.v2.fs.ResourceFileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.Closeable;
import java.util.List;

public interface ResourceManager extends ResourceProvider, Closeable {
	@NotNull
	ResourceFileSystem getFileSystem();

	@NotNull
	ReloadStatus reload();

	@NotNull
	@UnmodifiableView
	List<ResourcePack> getResourcePacks();

	void setPackPriority(@NotNull ResourcePack pack, int priority);

	int getPackPriority(@NotNull ResourcePack pack);

	void registerResourcePack(@NotNull ResourcePack pack);

	void unregisterResourcePack(@NotNull ResourcePack pack);
}
