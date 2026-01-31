package io.github.wsyong11.gameforge.framework.system.audio.impl.simple;

import io.github.wsyong11.gameforge.framework.system.audio.AudioDevice;
import io.github.wsyong11.gameforge.framework.system.audio.AudioDeviceIdentity;
import org.jetbrains.annotations.NotNull;

public class EmptyAudioDevice implements AudioDevice {
	private volatile boolean closed;

	public EmptyAudioDevice() {
		this.closed = false;
	}

	@NotNull
	@Override
	public AudioDeviceIdentity getIdentity() {
		return NamedAudioDeviceIdentity.EMPTY;
	}

	@Override
	public boolean isClosed() {
		return this.closed;
	}

	@Override
	public void close() {
		this.closed = true;
	}
}
