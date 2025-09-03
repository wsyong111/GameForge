package io.github.wsyong11.gameforge.framework.system.resource.pack;

import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Set;

// TODO: 2025/9/3 Add getInfo method
public interface ResourcePack {
//	@NotNull
//	ResourcePackInfo getInfo();

	@NotNull
	String getId();

	@Nullable
	Resource getResource(@NotNull String path);

	@NotNull
	@UnmodifiableView
	Set<String> listResources();
}
