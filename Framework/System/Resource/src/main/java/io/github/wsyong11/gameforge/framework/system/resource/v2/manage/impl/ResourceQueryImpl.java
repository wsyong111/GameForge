package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl;

import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.query.QuerySnapshot;
import io.github.wsyong11.gameforge.framework.system.resource.v2.query.ResourceQuery;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class ResourceQueryImpl implements ResourceQuery {


	@Override
	public @NotNull ResourceQuery ofQuery(@NotNull QuerySnapshot snapshot) {
		return null;
	}

	@Override
	public @NotNull QuerySnapshot toQuery() {
		return null;
	}

	@Override
	public @NotNull ResourceQuery ofPredicate(@NotNull Predicate<Resource> condition) {
		return null;
	}

	@Override
	public @NotNull Stream<Resource> stream() {
		return Stream.empty();
	}
}
