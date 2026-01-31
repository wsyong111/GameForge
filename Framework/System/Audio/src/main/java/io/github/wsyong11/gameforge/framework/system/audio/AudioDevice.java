package io.github.wsyong11.gameforge.framework.system.audio;

import org.jetbrains.annotations.NotNull;

public interface AudioDevice extends AutoCloseable {
	@NotNull
	AudioDeviceIdentity getIdentity();

	boolean isClosed();

	@Override
	void close();
}
