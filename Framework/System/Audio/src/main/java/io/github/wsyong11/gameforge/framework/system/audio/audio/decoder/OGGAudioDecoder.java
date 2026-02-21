package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.mime.MimeTypes;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class OGGAudioDecoder implements AudioDecoder {
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

	private final InputStream dataStream;

	private final Set<AudioDecodeHint> hints;
	private final boolean streamingDecode;
	private final boolean preloadMetadata;

	public OGGAudioDecoder(@NotNull DecodeInfo info) {
		Objects.requireNonNull(info, "info is null");

		if (!MimeTypes.Audio.OGG.equals(info.getMime()))
			throw new UnsupportedOperationException("Unsupported mime type " + info.getMime());

		this.dataStream = info.openStream();

		Set<AudioDecodeHint> hints = info.getHints();
		this.streamingDecode = hints.contains(AudioDecodeHint.STREAMING);
		this.preloadMetadata = hints.contains(AudioDecodeHint.PRELOAD_METADATA);

		this.hints = hints
			.stream()
			.filter(h -> h == AudioDecodeHint.STREAMING || h == AudioDecodeHint.PRELOAD_METADATA)
			.collect(Collectors.toUnmodifiableSet());
	}

	@NotNull
	@UnmodifiableView
	@Override
	public Set<AudioDecodeHint> getActivatedHints() {

	}

	protected

	@NotNull
	@Override
	public AudioMetadata getMetadata() throws AudioDecodeException {
		return null;
	}

	@NotNull
	@Override
	public PCMStream decode() throws AudioDecodeException {
		return null;
	}

	@Override
	public void close() throws IOException {

	}
}
