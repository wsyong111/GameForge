package io.github.wsyong11.gameforge.framework.system.resource.v2.transform;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.query.ResourceQuery;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

public interface TransformContext {
	void replaceResource(@NotNull ResourcePath path, @NotNull UnaryOperator<Resource> transformer);

	void addResource(@NotNull ResourcePath path, @NotNull Resource resource);

	void removeResource(@NotNull ResourcePath path);

	@NotNull
	ResourceQuery query();
}
