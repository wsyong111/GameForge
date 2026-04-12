package io.github.wsyong11.gameforge.framework.system.resource.v2.manage;

import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourceSelector;
import org.jetbrains.annotations.NotNull;

public interface ResourceList extends Iterable<Resource> {
	@NotNull
	ResourceSelector select();

	int count();
}
