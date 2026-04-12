package io.github.wsyong11.gameforge.framework.system.resource.v2.transform;

import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ResourceTransformer {
	@NotNull
	List<Resource> transform(@NotNull Resource resource, @NotNull TransformContext context);
}
