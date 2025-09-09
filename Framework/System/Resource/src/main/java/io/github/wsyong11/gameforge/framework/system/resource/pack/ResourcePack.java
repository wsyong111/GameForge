package io.github.wsyong11.gameforge.framework.system.resource.pack;

import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Closeable;
import java.util.Set;

// TODO: 2025/9/3 Add getInfo method
public interface ResourcePack extends Closeable {
//	@NotNull
//	ResourcePackInfo getInfo();

	boolean isClosed();

	@NotNull
	String getId();

	@Nullable
	Resource getResource(@NotNull ResourcePath path);

	@NotNull
	@Unmodifiable
	Set<ResourcePath> listResources();
}
