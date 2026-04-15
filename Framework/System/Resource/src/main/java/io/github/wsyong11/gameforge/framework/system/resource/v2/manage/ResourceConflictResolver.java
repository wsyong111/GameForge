package io.github.wsyong11.gameforge.framework.system.resource.v2.manage;

import io.github.wsyong11.gameforge.framework.spi.ExtensionType;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ResourceConflictResolver {
	ExtensionType<ResourceConflictResolver> TYPE = ExtensionType.of(ResourceConflictResolver.class);

	boolean isSupportPath(@NotNull ResourcePath path);

	@Nullable
	Resource resolve(@NotNull ResourcePath path, @NotNull List<ResourcePack> packs);
}
