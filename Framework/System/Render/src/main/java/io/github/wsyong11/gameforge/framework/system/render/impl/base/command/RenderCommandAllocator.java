package io.github.wsyong11.gameforge.framework.system.render.impl.base.command;

import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.pool.RenderCommandPool;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RenderCommandAllocator {
	private static final ThreadLocal<RenderCommandPool> LOCAL_POOL = ThreadLocal.withInitial(RenderCommandPool::new);

	private static final RenderCommandPool GLOBAL_POOL = new RenderCommandPool();


}
