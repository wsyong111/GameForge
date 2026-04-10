package io.github.wsyong11.gameforge.framework.system.resource.v2.manage;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.Predicate;

public interface ResourceGraph {
	@Nullable
	Resource put(@NotNull ResourcePath path, @NotNull Resource resource);

	@Nullable
	Resource get(@NotNull ResourcePath path);

	boolean exist(@NotNull ResourcePath path);

	@NotNull
	@Unmodifiable
	List<Resource> list(@NotNull Predicate<ResourcePath> filter);
}
