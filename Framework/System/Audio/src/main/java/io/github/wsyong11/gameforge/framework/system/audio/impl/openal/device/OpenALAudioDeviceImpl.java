package io.github.wsyong11.gameforge.framework.system.audio.impl.openal.device;

import io.github.wsyong11.gameforge.framework.system.audio.AudioDeviceIdentity;
import io.github.wsyong11.gameforge.framework.system.audio.ex.AudioDeviceClosedException;
import io.github.wsyong11.gameforge.framework.system.audio.ex.AudioDeviceException;
import io.github.wsyong11.gameforge.framework.system.audio.ex.AudioDeviceOpenException;
import io.github.wsyong11.gameforge.framework.system.audio.impl.openal.context.OpenALContext;
import io.github.wsyong11.gameforge.framework.system.audio.impl.openal.context.OpenALContextImpl;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.openal.ALC11.alcCloseDevice;
import static org.lwjgl.openal.ALC11.alcOpenDevice;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenALAudioDeviceImpl implements OpenALAudioDevice {
	private static final Logger LOGGER = Log.getLogger();

	private final long handler;
	private final AudioDeviceIdentity identity;

	private boolean closed;

	private final List<OpenALContext> contexts;

	public OpenALAudioDeviceImpl(@NotNull AudioDeviceIdentity identity) throws AudioDeviceOpenException {
		Objects.requireNonNull(identity, "identity is null");

		this.handler = alcOpenDevice(identity.getName());
		if (this.handler == NULL)
			throw new AudioDeviceOpenException("Failed to open the audio device " + identity);

		this.identity = identity;

		this.closed = false;

		this.contexts = new ArrayList<>();
	}

	@NotNull
	@Override
	public AudioDeviceIdentity getIdentity() {
		return this.identity;
	}

	@Override
	public boolean isClosed() {
		return this.closed;
	}

	private void checkState() {
		if (this.closed)
			throw new AudioDeviceClosedException("Audio device closed");
	}

	public long getHandler() {
		return this.handler;
	}

	@Override
	@NotNull
	public OpenALContext createContext() throws AudioDeviceException {
		this.checkState();

		OpenALContext context = new OpenALContextImpl(this.handler, this.identity);
		this.contexts.add(context);
		return context;
	}

	@Override
	public void close() {
		if (this.closed)
			return;
		this.closed = true;

		for (OpenALContext context : this.contexts)
			context.close();
		this.contexts.clear();

		if (!alcCloseDevice(this.handler)) {
			LOGGER.warn("Failed to close the device {} {}",
				this.identity,
				String.format("0x%08X", this.handler));
		} else {
			LOGGER.trace("Closed device {}", this.identity);
		}
	}

	@Override
	public String toString() {
		return "OpenALDevice{%s 0x%08X}".formatted(
			this.identity,
			this.handler);
	}
}
