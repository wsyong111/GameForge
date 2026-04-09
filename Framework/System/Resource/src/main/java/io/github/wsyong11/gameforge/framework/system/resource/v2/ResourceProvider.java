package io.github.wsyong11.gameforge.framework.system.resource.v2;

import io.github.wsyong11.gameforge.framework.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface ResourceProvider {
	@Nullable
	Resource getResource(@NotNull Identifier location);

	@Nullable
	@Unmodifiable
	List<Resource> getAllResources(@NotNull Identifier location);

	@Nullable
	@Unmodifiable
	List<String> listResources(@NotNull Identifier location);

	boolean hasResource(@NotNull Identifier location);
}
