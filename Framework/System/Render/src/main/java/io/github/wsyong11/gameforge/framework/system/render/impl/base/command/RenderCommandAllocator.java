package io.github.wsyong11.gameforge.framework.system.render.impl.base.command;

import io.github.wsyong11.gameforge.framework.annotation.ThreadSensitive;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.command.RenderCommand;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.pool.GlobalRenderCommandPool;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.pool.RenderCommandPool;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.pool.ThreadRenderCommandPool;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// TODO: 2026/3/30 Use env
@UtilityClass
public class RenderCommandAllocator {
	private static final ThreadLocal<RenderCommandPool> LOCAL_POOL = ThreadLocal.withInitial(() ->
		new ThreadRenderCommandPool(128));

	private static final GlobalRenderCommandPool GLOBAL_POOL = new GlobalRenderCommandPool(128);

	@NotNull
	@ThreadSensitive
	public static <T extends RenderCommand> T alloc(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		RenderCommandPool localPool = LOCAL_POOL.get();
		T cmd = localPool.tryAcquire(type);
		if (cmd != null)
			return cmd;

		T globalCmd = GLOBAL_POOL.tryAcquire(type);
		if (globalCmd != null)
			return globalCmd;

		return RenderCommands.getFactory(type).get();
	}

	public static void free(@Nullable RenderCommand cmd) {
		if (cmd == null)
			return;

		RenderCommandPool localPool = LOCAL_POOL.get();
		if (localPool.tryRelease(cmd))
			return;

		GLOBAL_POOL.tryRelease(cmd);

		// Drop command
	}
}
