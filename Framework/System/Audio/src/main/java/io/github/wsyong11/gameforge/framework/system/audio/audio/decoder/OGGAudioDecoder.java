package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.mime.MimeTypes;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioCategory;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeIOException;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecoderClosedException;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.io.SeekableInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.*;

public class OGGAudioDecoder implements AudioDecoder {
	private static final Logger LOGGER = Log.getLogger();

	private static final int SEEK_BUFFER_SIZE = 1024 * 48; // 48KiB

	private static final int MIN_HEADER_BUFFER_SIZE = 2048; // 2KiB
	private static final int HEADER_BUFFER_STEP = 1024; // 1KiB
	private static final int MAX_HEADER_BUFFER_SIZE = 1024 * 64; // 64KiB

	private static final int MIN_DATA_BUFFER_SIZE = 1024 * 16; // 16KiB
	private static final int MAX_DATA_BUFFER_SIZE = 1024 * 1024 * 4; // 4MiB

	public static final AudioDecoderFactory FACTORY = new AudioDecoderFactory() {
		@NotNull
		@Override
		public AudioDecoder create(@NotNull DecodeInfo info) {
			Objects.requireNonNull(info, "info is null");
			return new OGGAudioDecoder(info);
		}

		@NotNull
		@Override
		public Set<MimeType> getSupportMimes() {
			return Set.of(MimeTypes.Audio.OGG);
		}

		@NotNull
		@Unmodifiable
		@Override
		public Set<AudioDecodeHint> getSupportHints() {
			return Set.of(AudioDecodeHint.STREAMING, AudioDecodeHint.PRELOAD_METADATA);
		}
	};

	private final SeekableInputStream dataStream;

	private final Set<AudioDecodeHint> hints;
	private final boolean streamingDecode;
	private final boolean preloadMetadata;

	private long decoderHandler;
	private ByteBuffer headerDataBuffer;

	private AudioMetadata metadata;

	private boolean closed;

	public OGGAudioDecoder(@NotNull DecodeInfo info) {
		Objects.requireNonNull(info, "info is null");

		if (!MimeTypes.Audio.OGG.equals(info.getMime()))
			throw new UnsupportedOperationException("Unsupported mime type " + info.getMime());

		this.dataStream = new SeekableInputStream(info.openStream(), SEEK_BUFFER_SIZE);

		Set<AudioDecodeHint> hints = info.getHints();
		this.streamingDecode = hints.contains(AudioDecodeHint.STREAMING);
		this.preloadMetadata = hints.contains(AudioDecodeHint.PRELOAD_METADATA);

		this.hints = hints
			.stream()
			.filter(h -> h == AudioDecodeHint.STREAMING || h == AudioDecodeHint.PRELOAD_METADATA)
			.collect(Collectors.toUnmodifiableSet());

		this.decoderHandler = NULL;
		this.headerDataBuffer = null;
		this.metadata = null;

		this.closed = false;
	}

	private void openDecoder() throws AudioDecodeException {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer errorBuf = stack.mallocInt(1);
			IntBuffer consumedDataByteBuf = stack.mallocInt(1);

			int bufferSize = MIN_HEADER_BUFFER_SIZE;

			long handler;
			ByteBuffer headerBuffer;
			while (true) {
				errorBuf.put(0, 0);
				consumedDataByteBuf.put(0, 0);
				this.dataStream.seek(0L);

				byte[] headerBufferTemp = new byte[bufferSize];

				int consumedHeaderSize = this.dataStream.read(headerBufferTemp);
				if (consumedHeaderSize == -1)
					throw new EOFException("Data stream end");

				headerBuffer = memAlloc(bufferSize);
				headerBuffer.put(headerBufferTemp, 0, consumedHeaderSize);
				headerBuffer.flip();

				handler = stb_vorbis_open_pushdata(
					headerBuffer,
					consumedDataByteBuf,
					errorBuf,
					null
				);

				if (handler != NULL)
					break;

				int error = errorBuf.get(0);
				if (error == VORBIS_need_more_data) {
					memFree(headerBuffer);
					bufferSize += HEADER_BUFFER_STEP;

					if (bufferSize > MAX_HEADER_BUFFER_SIZE)
						throw new AudioDecodeException("Header too large");
				} else if (error != VORBIS__no_error) {
					throw new AudioDecodeException("Cannot open stb vorbis decoder: " + error);
				}
			}

			this.headerDataBuffer = headerBuffer;
			this.decoderHandler = handler;
			this.dataStream.seek(consumedDataByteBuf.get(0));
		} catch (IOException e) {
			throw new AudioDecodeIOException(e);
		}
	}

	private void ensureDecoder() throws AudioDecodeException {
		if (this.closed)
			throw new AudioDecoderClosedException("Audio decoder closed");

		if (this.decoderHandler != NULL)
			return;

		this.openDecoder();
	}

	private void ensureMetadata() throws AudioDecodeException {
		this.ensureDecoder();

		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			stb_vorbis_get_info(this.decoderHandler, info);
			this.metadata = new OggMetadata(info);
		}
	}

	@Override
	public void preload() {
		if (this.preloadMetadata) {
			try {
				this.ensureMetadata();
			} catch (AudioDecodeException e) {
				LOGGER.warn("Failed preload metadata", e);
			}
		}
	}

	@NotNull
	@UnmodifiableView
	@Override
	public Set<AudioDecodeHint> getActivatedHints() {
		return this.hints;
	}

	@NotNull
	@Override
	public AudioMetadata getMetadata() throws AudioDecodeException {
		this.ensureMetadata();
		return this.metadata;
	}

	@NotNull
	@Override
	public PCMStream decode() throws AudioDecodeException {
		return null;
	}

	@Override
	public void close() throws IOException {
		if (this.closed)
			return;
		this.closed = true;

		if (this.decoderHandler != NULL)
			stb_vorbis_close(this.decoderHandler);

		memFree(this.headerDataBuffer);
		this.dataStream.close();
	}

	private static class OggMetadata implements AudioMetadata {
		private final int sampleRate;
		private final int channels;

		public OggMetadata(@NotNull STBVorbisInfo info) {
			Objects.requireNonNull(info, "info is null");

			this.sampleRate= info.sample_rate();
			this.channels = info.channels();
		}

		@Override
		public int getSampleRate() {
			return 0;
		}

		@Override
		public int getChannels() {
			return 0;
		}

		@Override
		public int getBitDepth() {
			return 0;
		}

		@Override
		public long getTotalSamples() {
			return 0;
		}

		@Override
		public boolean isStreamable() {
			return false;
		}

		@Override
		public boolean isSeekable() {
			return false;
		}

		@NotNull
		@Override
		public AudioCategory getCategory() {
			return null;
		}

		@Override
		public <T> @Nullable T get(@NotNull Key<T> key) {
			return null;
		}

		@Override
		public @NotNull @UnmodifiableView Set<Key<?>> getKeys() {
			return Set.of();
		}
	}
}
