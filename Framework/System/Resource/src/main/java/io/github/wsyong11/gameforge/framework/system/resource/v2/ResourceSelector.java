package io.github.wsyong11.gameforge.framework.system.resource.v2;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface ResourceSelector {
	@NotNull
	ResourceSelector ofPack(@NotNull ResourcePack pack);

	@NotNull
	ResourceSelector ofPath(@NotNull ResourcePath path);

	@NotNull
	ResourceSelector ofPredicate(@NotNull Predicate<Resource> condition);

	@NotNull
	ResourceSelector ofPattern(@Language("RegExp") @NotNull String pattern);

	@NotNull
	ResourceSelector ofPattern(@NotNull Pattern pattern);

	@NotNull
	Stream<Resource> stream();

	void forEach(@NotNull Consumer<Resource> action);

	@NotNull
	List<Resource> list();

	@NotNull
	Iterator<Resource> iterator();

	@NotNull
	Optional<Resource> first();

	int count();

	@Nullable
	Resource firstWithoutOptional();
}
