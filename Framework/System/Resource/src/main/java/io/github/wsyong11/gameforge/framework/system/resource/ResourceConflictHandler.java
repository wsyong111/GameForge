package io.github.wsyong11.gameforge.framework.system.resource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ResourceConflictHandler {
	@Nullable
	Resource processConflict(@NotNull List<Resource> resources);
}
