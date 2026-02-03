package io.github.wsyong11.gameforge.framework.system.audio.provider;

import io.github.wsyong11.gameforge.framework.system.audio.engine.AudioEngine;
import io.github.wsyong11.gameforge.framework.system.audio.engine.AudioEngineContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@FunctionalInterface
public interface AudioEngineProvider {
	@NotNull
	Function<AudioEngineContext, AudioEngine> getFactory();
}
