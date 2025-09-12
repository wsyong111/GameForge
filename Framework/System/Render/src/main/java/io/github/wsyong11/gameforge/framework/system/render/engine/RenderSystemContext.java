package io.github.wsyong11.gameforge.framework.system.render.engine;

import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import org.jetbrains.annotations.NotNull;

public interface RenderSystemContext {
	@NotNull
	TaskHandler getTaskHandler();
}
