package io.github.wsyong11.gameforge.framework.system.resource;

import io.github.wsyong11.gameforge.framework.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ResourceProvider {
	@Nullable
	Resource getResource(@NotNull Identifier name);

	default boolean hasResource(@NotNull Identifier name) {
		return this.getResource(name) != null;
	}
}
