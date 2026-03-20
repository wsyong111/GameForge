package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio.decode;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioStatus;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import io.github.wsyong11.gameforge.framework.system.audio.audio.stream.AudioStream;
import io.github.wsyong11.gameforge.framework.system.audio.audio.stream.AudioStreamStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;

public class DecodeContextAudio implements Audio {
	private final DecodeContext context;

	public DecodeContextAudio(@NotNull DecodeContext context) {
		Objects.requireNonNull(context, "context is null");

		this.context = context;
	}

	@NotNull
	@Override
	public Identifier getLocation() {
		return this.context.getIdentifier();
	}

	@NotNull
	@Override
	public AudioMetadata getMetadata() {
		if (this.context.getStatus() != AudioStatus.READY)
			throw new IllegalStateException("Cannot get metadata");

		return this.context.getMetadata();
	}

	@NotNull
	@Override
	public AudioStatus getStatus() {
		return this.context.getStatus();
	}

	@NotNull
	@Override
	public AudioStream newStream() {
		return new AudioStreamImpl(this, this.context);
	}

	@Override
	public void registerStatusCallback(@NotNull StatusCallback callback) {
		Objects.requireNonNull(callback, "callback is null");
		this.context.registerStatusCallback(callback);
	}

	@Override
	public void unregisterStatusCallback(@NotNull StatusCallback callback) {
		Objects.requireNonNull(callback, "callback is null");
		this.context.unregisterStatusCallback(callback);
	}

	@Override
	public void close() {
		this.context.close();
	}

	private static class AudioStreamImpl implements AudioStream {
		private final Audio audio;
		private final DecodeContext context;

		private int position;

		public AudioStreamImpl(@NotNull Audio audio, @NotNull DecodeContext context) {
			Objects.requireNonNull(audio, "audio is null");
			Objects.requireNonNull(context, "context is null");

			this.audio = audio;
			this.context = context;

			this.position = 0;
		}

		@NotNull
		@Override
		public Audio getAudio() {
			return this.audio;
		}

		@NotNull
		@Override
		public AudioStreamStatus getStatus() {
			DecodeContext.DecodeState decodeState = this.context.getDecodeState();
			if (decodeState == DecodeContext.DecodeState.FAILED)
				return AudioStreamStatus.ERROR;

			if (decodeState == DecodeContext.DecodeState.COMPLETE) {
				AudioMetadata metadata = this.context.getMetadata();
				long totalFrames = metadata.getTotalFrames();
				if (totalFrames != -1 && this.position >= totalFrames)
					return AudioStreamStatus.FINISHED;

				return AudioStreamStatus.PLAYABLE;
			}

			if (decodeState == DecodeContext.DecodeState.IDLE)
				return AudioStreamStatus.PLAYABLE;

			if (decodeState == DecodeContext.DecodeState.DECODING)
				return AudioStreamStatus.BUFFERING;

			return AudioStreamStatus.READY;
		}

		@Override
		public long getPosition() {
			return this.position;
		}

		@Override
		public long getAvailable() {
			return Math.max(0, this.context.getBufferSize() - this.position);
		}

		@Override
		public void seek(long frame) throws AudioDecodeException {
			this.context.checkDecodeException();

			if (frame > Integer.MAX_VALUE)
				throw new IllegalArgumentException("Frame index is too big");

			int pos = (int) frame;
			this.context.requireData(pos);
			this.position = pos;
		}

		@Override
		public boolean isSeekSupport() {
			return true;
		}

		@Override
		public void reset() {
			this.position = 0;
		}

		@Override
		public int read(@NotNull FloatBuffer buffer, int maxFrames) throws AudioDecodeException {
			Objects.requireNonNull(buffer, "buffer is null");

			int read;
			try {
				read = this.context.readData(this.position, buffer, maxFrames);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new AudioDecodeException("Thread interrupted", e);
			}

			if (read == -1)
				return -1;

			this.position += read;
			return read;
		}

		@Override
		public void close() {

		}
	}
}
