package io.github.wsyong11.gameforge.framework.system.audio.impl.openal;

import io.github.wsyong11.gameforge.framework.system.audio.ex.AudioContextException;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;
import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenALContext implements AutoCloseable {
	private static final Logger LOGGER = Log.getLogger();

	private static final int[] DEFAULT_ATTRIBUTE_LIST = {0};

	private final OpenALAudioDevice device;

	private long context;

	public OpenALContext(@NotNull OpenALAudioDevice device) {
		Objects.requireNonNull(device, "device is null");

		this.device = device;

		this.context = alcCreateContext(device.getHandler(), DEFAULT_ATTRIBUTE_LIST);
		if (this.context == NULL)
			throw new AudioContextException("Failed to create the context use device " + device.getIdentity());

		LOGGER.trace("Created context with the device {}: {}",
			device.getIdentity(),
			lazy(() -> String.format("0x%08X", this.context)));
	}

	@Override
	public void close() {
		if (this.context == NULL)
			return;

		alcDestroyContext(this.context);
		int code = alcGetError(this.device.getHandler());
		if (code != ALC_NO_ERROR)
			LOGGER.warn("Failed to destroy context {} from device {}: {}",
				String.format("0x%08X", this.context),
				this.device.getIdentity(),
				ErrorUtil.getErrorType(code));

		this.context = NULL;
	}

	public void use() {
		alcMakeContextCurrent(this.context);
	}
}
