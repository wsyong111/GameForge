package io.github.wsyong11.gameforge.framework.system.audio.engine;

import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import org.jetbrains.annotations.NotNull;

public interface AudioEngineContext {
	@NotNull
	ResourceProvider getResourceProvider();

	@NotNull
	TaskHandler getAudioTaskHandler();
}
