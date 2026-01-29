package io.github.wsyong11.gameforge.framework.system.audio.provider;

import io.github.wsyong11.gameforge.framework.system.audio.AudioEngine;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@FunctionalInterface
public interface AudioEngineProvider {
	@NotNull
	Supplier<AudioEngine> getFactory();
}
