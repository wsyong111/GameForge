package io.github.wsyong11.gameforge.framework.system.resource.v2.transform;

import io.github.wsyong11.gameforge.framework.spi.Extension;
import io.github.wsyong11.gameforge.framework.spi.ExtensionType;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ResourceTransformer {
	ExtensionType<ResourceTransformer> TYPE = ExtensionType.of(ResourceTransformer.class);

	@NotNull
	void transform(@NotNull Resource resource, @NotNull TransformContext context);
}
