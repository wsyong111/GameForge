package io.github.wsyong11.gameforge.framework.system.resource.v2.manage;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourceProvider;
import io.github.wsyong11.gameforge.framework.system.resource.v2.fs.ResourceFileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.Closeable;
import java.util.List;

/*
Source -> ResourcePack -> Transformer -> MergeResource -> ResolveConflict -> Index -> ResourceManager
 */
public interface ResourceManager extends ResourceProvider, Closeable {
	@NotNull
	ResourceFileSystem getFileSystem();

	@Nullable
	Identifier toIdentifier(@NotNull ResourcePath path);

	@NotNull
	ResourcePath toPath(@NotNull Identifier id);

	// -------------------------------------------------------------------------------------------------------------- //

	void registerConflictResolver(@NotNull ResourceConflictResolver resolver);

	void unregisterConflictResolver(@NotNull ResourceConflictResolver resolver);

	void registerResourceTransformer(@NotNull ResourceTransformer transformer);

	void unregisterResourceTransformer(@NotNull ResourceTransformer transformer);

	@NotNull
	ReloadStatus reload();

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@UnmodifiableView
	List<ResourcePack> getResourcePacks();

	void setPackPriority(@NotNull ResourcePack pack, int priority);

	int getPackPriority(@NotNull ResourcePack pack);

	void registerResourcePack(@NotNull ResourcePack pack);

	void unregisterResourcePack(@NotNull ResourcePack pack);
}
