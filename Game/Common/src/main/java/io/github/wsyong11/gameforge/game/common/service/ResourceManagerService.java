package io.github.wsyong11.gameforge.game.common.service;

import io.github.wsyong11.gameforge.framework.annotation.UnsafeAPI;
import io.github.wsyong11.gameforge.framework.system.resource.manage.IResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ResourceManagerService extends StubService<ResourceManager>, IResourceManager {
	@NotNull
	@Override
	default String getServiceName() {
		return ResourceManagerService.class.getName();
	}
}
