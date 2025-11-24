package io.github.wsyong11.gameforge.framework.system.resource;

import io.github.wsyong11.gameforge.framework.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface ResourceProvider {
	@Nullable
	Resource getResource(@NotNull Identifier name);

	@NotNull
	@Unmodifiable
	List<Resource> getResources(@NotNull Identifier name);

	@NotNull
	default Optional<Resource> getResourceOptional(@NotNull Identifier name) {
		Objects.requireNonNull(name, "name is null");
		return Optional.ofNullable(this.getResource(name));
	}

	default boolean hasResource(@NotNull Identifier name) {
		return this.getResource(name) != null;
	}
}
