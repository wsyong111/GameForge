package io.github.wsyong11.gameforge.framework.system.resource.v2.transform;

import io.github.wsyong11.gameforge.framework.spi.ExtensionType;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import org.jetbrains.annotations.NotNull;

public interface ResourceTransformer {
	ExtensionType<ResourceTransformer> TYPE = ExtensionType.of(ResourceTransformer.class);

	void transform(@NotNull Resource resource, @NotNull TransformContext context);
}
