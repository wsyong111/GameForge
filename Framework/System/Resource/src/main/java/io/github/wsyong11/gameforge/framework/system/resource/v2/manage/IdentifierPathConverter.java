package io.github.wsyong11.gameforge.framework.system.resource.v2.manage;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.spi.ExtensionType;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourcePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IdentifierPathConverter {
	ExtensionType<IdentifierPathConverter> TYPE = ExtensionType.of(IdentifierPathConverter.class);

	@Nullable
	ResourcePath toPath(@NotNull Identifier id);

	@Nullable
	Identifier toId(@NotNull ResourcePath path);
}
