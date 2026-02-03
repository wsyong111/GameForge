package io.github.wsyong11.gameforge.framework.system.audio.impl.openal.context;

import io.github.wsyong11.gameforge.framework.system.audio.AudioDeviceIdentity;
import io.github.wsyong11.gameforge.framework.system.audio.ex.AudioContextException;
import io.github.wsyong11.gameforge.framework.system.audio.impl.openal.ErrorUtil;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;
import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenALContextImpl implements OpenALContext {
	private static final Logger LOGGER = Log.getLogger();

	private static final int[] DEFAULT_ATTRIBUTE_LIST = {0};

	private final long deviceHandler;
	private final AudioDeviceIdentity identity;

	private long contextHandler;

	public OpenALContextImpl(long handler, @NotNull AudioDeviceIdentity identity) {
		Objects.requireNonNull(identity, "identity is null");

		this.deviceHandler = handler;
		this.identity = identity;

		this.contextHandler = alcCreateContext(handler, DEFAULT_ATTRIBUTE_LIST);
		if (this.contextHandler == NULL)
			throw new AudioContextException("Failed to create the context use device " + identity);

		LOGGER.trace("Created context with the device {}: {}",
			identity,
			lazy(() -> String.format("0x%08X", this.contextHandler)));
	}

	@Override
	public void close() {
		if (this.contextHandler == NULL)
			return;

		alcDestroyContext(this.contextHandler);
		int code = alcGetError(this.deviceHandler);
		if (code != ALC_NO_ERROR)
			LOGGER.warn("Failed to destroy context {} from device {}: {}",
				String.format("0x%08X", this.contextHandler),
				this.identity,
				ErrorUtil.getErrorType(code));

		this.contextHandler = NULL;
	}

	@Override
	public void use() {
		alcMakeContextCurrent(this.contextHandler);
	}

	@Override
	public String toString() {
		return "OpenALContext{%s 0x%08X}".formatted(
			this.identity,
			this.contextHandler);
	}
}
