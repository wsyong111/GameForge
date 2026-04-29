package io.github.wsyong11.gameforge.framework.system.resource.v2.manage;

import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.query.ResourceQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface ResourceGraph {
	@Nullable
	Resource put(@NotNull ResourcePath path, @NotNull Resource resource);

	@Nullable
	Resource get(@NotNull ResourcePath path);

	boolean remove(@NotNull ResourcePath path);

	boolean exist(@NotNull ResourcePath path);

	boolean isDir(@NotNull ResourcePath path);

	boolean isEntry(@NotNull ResourcePath path);

	@Nullable
	@Unmodifiable
	List<ResourcePath> list(@NotNull ResourcePath path);

	@NotNull
	@Unmodifiable
	List<Resource> listAll();

//	boolean isFrozen();

	int size();

	void clear();

	@NotNull
	ResourceGraph copy();

	@NotNull
	ResourceQuery query();
}
