package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.ogg;

import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AbstractAudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import io.github.wsyong11.gameforge.util.io.SeekableInputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.stb.STBVorbis.stb_vorbis_close;
import static org.lwjgl.system.MemoryUtil.NULL;

public class StreamingOggAudioDecoder extends AbstractAudioDecoder {
	private static final int MAX_HEADER_SIZE = 1024 * 64; // 64KiB
	private static final int INITIAL_HEADER_SIZE = 1024 * 4; // 4KiB

	private SeekableInputStream dataStream;

	private long decoderHandler;

	private boolean closed;

	public StreamingOggAudioDecoder(@NotNull DecodeInfo info) {
		super(info);

		this.dataStream = null;

		this.decoderHandler = NULL;

		this.closed = false;
	}

	private void openDecoder() {
		this.dataStream = new SeekableInputStream(this.getInfo().openStream(), MAX_HEADER_SIZE * 2);
	}

	@Override
	protected boolean isSupportHint(@NotNull AudioDecodeHint hint) {
		return hint == AudioDecodeHint.STREAMING
			|| hint == AudioDecodeHint.PRELOAD_METADATA;
	}

	@NotNull
	@Override
	public AudioMetadata getMetadata() throws AudioDecodeException {
		return null;
	}

	@Override
	public int decode(@NotNull FloatBuffer buffer, int maxFrame) throws AudioDecodeException {
		return 0;
	}

	@Override
	public void close() throws IOException {
		if (this.closed)
			return;
		this.closed = true;

		if (this.decoderHandler != NULL)
			stb_vorbis_close(this.decoderHandler);

		if (this.dataStream != null)
			this.dataStream.close();
	}
}
