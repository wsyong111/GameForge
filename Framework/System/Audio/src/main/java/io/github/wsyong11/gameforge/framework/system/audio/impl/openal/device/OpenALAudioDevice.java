package io.github.wsyong11.gameforge.framework.system.audio.impl.openal.device;

import io.github.wsyong11.gameforge.framework.system.audio.AudioDevice;
import io.github.wsyong11.gameforge.framework.system.audio.ex.AudioDeviceException;
import io.github.wsyong11.gameforge.framework.system.audio.impl.openal.context.OpenALContext;
import org.jetbrains.annotations.NotNull;

public interface OpenALAudioDevice extends AudioDevice {
	@NotNull
	OpenALContext createContext() throws AudioDeviceException;
}
