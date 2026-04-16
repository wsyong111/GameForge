package io.github.wsyong11.gameforge.framework.system.resource.v2.manage;

import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.query.ResourceQuery;
import org.jetbrains.annotations.NotNull;

public interface ResourceList extends Iterable<Resource> {
	@NotNull
	ResourceQuery query();

	int count();
}
