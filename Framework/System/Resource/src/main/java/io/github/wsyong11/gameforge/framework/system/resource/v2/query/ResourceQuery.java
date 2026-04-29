package io.github.wsyong11.gameforge.framework.system.resource.v2.query;

import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface ResourceQuery {
	@NotNull
	ResourceQuery ofStartPath(@NotNull ResourcePath path);

	@NotNull
	ResourceQuery ofPath(@NotNull Predicate<ResourcePath> condition);

	@NotNull
	ResourceQuery ofPack(@NotNull Predicate<ResourcePack> condition);

	@NotNull
	ResourceQuery ofPattern(@Language("RegExp") @NotNull String pattern);

	@NotNull
	ResourceQuery ofPattern(@NotNull Pattern pattern);

	@NotNull
	ResourceQuery ofNamePattern(@Language("RegExp") @NotNull String pattern);

	@NotNull
	ResourceQuery ofNamePattern(@NotNull Pattern pattern);

	@NotNull
	ResourceQuery ofPredicate(@NotNull Predicate<Resource> condition);

	@NotNull
	Stream<Resource> stream();

	default void forEach(@NotNull Consumer<Resource> action) {
		Objects.requireNonNull(action, "action is null");
		this.stream().forEach(action);
	}

	@NotNull
	@Unmodifiable
	default List<Resource> list() {
		return this.stream().toList();
	}

	@NotNull
	default Iterator<Resource> iterator() {
		return this.stream().iterator();
	}

	@NotNull
	default Optional<Resource> first() {
		return this.stream().findFirst();
	}

	default int count() {
		return (int) this.stream().limit(Integer.MAX_VALUE).count();
	}
}
