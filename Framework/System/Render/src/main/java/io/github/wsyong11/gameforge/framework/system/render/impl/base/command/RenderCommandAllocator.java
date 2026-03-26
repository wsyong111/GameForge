package io.github.wsyong11.gameforge.framework.system.render.impl.base.command;

import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.pool.RenderCommandPool;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.pool.SimpleRenderCommandPool;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RenderCommandAllocator {
	private static final ThreadLocal<RenderCommandPool> LOCAL_POOL = ThreadLocal.withInitial(SimpleRenderCommandPool::new);

	private static final RenderCommandPool GLOBAL_POOL = new SimpleRenderCommandPool();


}
