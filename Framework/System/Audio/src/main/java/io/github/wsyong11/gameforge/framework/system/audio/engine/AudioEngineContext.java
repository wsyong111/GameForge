package io.github.wsyong11.gameforge.framework.system.audio.engine;

import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import org.jetbrains.annotations.NotNull;

public interface AudioEngineContext {
	@NotNull
	ResourceProvider getResourceProvider();
}
