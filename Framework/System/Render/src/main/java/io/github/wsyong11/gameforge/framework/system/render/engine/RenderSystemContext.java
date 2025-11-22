package io.github.wsyong11.gameforge.framework.system.render.engine;

import io.github.wsyong11.gameforge.framework.annotation.ThreadSensitive;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import org.jetbrains.annotations.NotNull;

public interface RenderSystemContext {
	boolean isDebug();

	@NotNull
	TaskHandler getTaskHandler();

	@ThreadSensitive
	boolean isRenderThread();

	default void assertRenderThread() {
		if (!this.isRenderThread())
			throw new IllegalThreadStateException("This thread is not a correct render thread");
	}
}
