package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioStatus;
import io.github.wsyong11.gameforge.framework.system.audio.audio.stream.AudioStream;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DecodedAudio extends AbstractAudio {
	private final Identifier location;

	@Nullable
	private volatile Provider provider;
	private volatile AudioStatus status;

	public DecodedAudio(@NotNull Identifier location, @NotNull TaskHandler taskHandler) {
		super(taskHandler);
		Objects.requireNonNull(location, "location is null");

		this.location = location;

		this.provider = null;
		this.status = AudioStatus.LOADING;
	}

	private void ensureLoadingStatus() {
		if (this.status != AudioStatus.LOADING)
			throw new IllegalStateException("Cannot set ready, need LOADING status, current status is " + this.status);
	}

	private void ensureReady() {
		if (this.status == AudioStatus.CLOSED)
			throw new IllegalStateException("This audio is closed");
		if (this.status != AudioStatus.READY)
			throw new IllegalStateException("This audio is not ready");
	}

	public synchronized void setStatusReady(@NotNull Provider provider) {
		Objects.requireNonNull(provider, "provider is null");

		this.ensureLoadingStatus();

		this.provider = provider;
		this.status = AudioStatus.READY;

		this.invokeCallback();
	}

	public synchronized void setStatusFailed() {
		this.ensureLoadingStatus();

		this.status = AudioStatus.FAILED;

		this.invokeCallback();
	}

	@NotNull
	@Override
	public Identifier getLocation() {
		return this.location;
	}

	@NotNull
	@Override
	public AudioMetadata getMetadata() {
		this.ensureReady();

		Provider provider = this.provider;
		assert provider != null;
		return provider.getMetadata();
	}

	@NotNull
	@Override
	public AudioStatus getStatus() {
		return this.status;
	}

	@NotNull
	@Override
	public AudioStream newStream() {
		this.ensureReady();
		Provider provider = this.provider;
		assert provider != null;
		return provider.newStream();
	}

	@Override
	public synchronized void close() {
		if (this.status == AudioStatus.CLOSED)
			return;
		this.status = AudioStatus.CLOSED;

		super.close();

		Provider provider = this.provider;
		if (provider != null)
			provider.close();

		this.invokeCallback();
	}

	public interface Provider {
		@NotNull
		AudioMetadata getMetadata();

		@NotNull
		AudioStream newStream();

		void close();
	}
}
