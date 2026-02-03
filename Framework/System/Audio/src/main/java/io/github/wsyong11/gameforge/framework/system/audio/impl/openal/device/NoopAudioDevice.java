package io.github.wsyong11.gameforge.framework.system.audio.impl.openal.device;

import io.github.wsyong11.gameforge.framework.system.audio.AudioDeviceIdentity;
import io.github.wsyong11.gameforge.framework.system.audio.impl.openal.context.NoopContext;
import io.github.wsyong11.gameforge.framework.system.audio.impl.openal.context.OpenALContext;
import io.github.wsyong11.gameforge.framework.system.audio.impl.simple.EmptyAudioDevice;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NoopAudioDevice extends EmptyAudioDevice implements OpenALAudioDevice {
	private final AudioDeviceIdentity identity;

	public NoopAudioDevice(@NotNull AudioDeviceIdentity identity) {
		Objects.requireNonNull(identity, "identity is null");
		this.identity = identity;
	}

	@NotNull
	@Override
	public AudioDeviceIdentity getIdentity() {
		return this.identity;
	}

	@NotNull
	@Override
	public OpenALContext createContext() {
		return new NoopContext();
	}

	@Override
	public String toString() {
		return "OpenALNoopDevice{" + this.identity + "}";
	}
}
