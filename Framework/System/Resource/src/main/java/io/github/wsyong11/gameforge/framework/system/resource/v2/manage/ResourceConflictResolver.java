package io.github.wsyong11.gameforge.framework.system.resource.v2.manage;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ResourceConflictResolver {
	default int getPriority() {
		return 0;
	}

	boolean isSupportPath(@NotNull ResourcePath path);

	@Nullable
	Resource resolve(@NotNull ResourcePath path, @NotNull List<Resource> candidates);
}
