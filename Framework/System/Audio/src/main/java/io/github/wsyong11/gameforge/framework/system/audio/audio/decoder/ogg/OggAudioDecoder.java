package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.ogg;

import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AbstractAudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.SimpleAudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeIOException;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecoderClosedException;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBVorbisComment;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.*;

public class OggAudioDecoder extends AbstractAudioDecoder {
	private static final Logger LOGGER = Log.getLogger();

	private static final int MAX_DATA_SIZE = (Integer.MAX_VALUE - 8) / 2;  // (MAX_INT - Array overhead) / 2
	private static final int DATA_CHUNK_SIZE = 1024 * 32; // 32KiB

	private int totalFrames;
	private int channels;
	private int frameRate;

	private ByteBuffer dataBuffer;

	private long decoderHandler;
	private boolean closed;

	private AudioMetadata metadata;

	public OggAudioDecoder(@NotNull DecodeInfo info) {
		super(info);

		this.totalFrames = 0;
		this.channels = 0;
		this.frameRate = 0;

		this.dataBuffer = null;

		this.decoderHandler = NULL;
		this.closed = false;

		this.metadata = null;
	}

	private void checkClosed() throws AudioDecoderClosedException {
		if (this.closed)
			throw new AudioDecoderClosedException();
	}

	private void openDecoder() throws AudioDecodeException {
		ByteArrayList dataList = new ByteArrayList();

		try (InputStream dataStream = this.getInfo().openStream()) {
			byte[] temp = new byte[DATA_CHUNK_SIZE];
			int size;
			while ((size = dataStream.read(temp)) != -1) {
				if (dataList.size() > MAX_DATA_SIZE - size)
					throw new AudioDecodeException("Audio data too big");

				dataList.addElements(dataList.size(), temp, 0, size);
			}
		} catch (IOException e) {
			throw new AudioDecodeIOException(e);
		}

		int dataSize = dataList.size();
		this.dataBuffer = memAlloc(dataSize);
		this.dataBuffer.put(dataList.elements(), 0, dataSize);
		this.dataBuffer.flip();

		// Free data
		dataList.clear();
		dataList.trim();

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer errorBuf = stack.mallocInt(1);

			this.decoderHandler = stb_vorbis_open_memory(this.dataBuffer, errorBuf, null);
			if (this.decoderHandler == NULL)
				throw new AudioDecodeException("Failed to open stb vorbis decoder: " + errorBuf.get());

			STBVorbisInfo vorbisInfo = STBVorbisInfo.malloc(stack);
			stb_vorbis_get_info(this.decoderHandler, vorbisInfo);

			this.channels = vorbisInfo.channels();
			this.frameRate = vorbisInfo.sample_rate();

			int sampleLength = stb_vorbis_stream_length_in_samples(this.decoderHandler);
			this.totalFrames = sampleLength;
		}
	}

	private void ensureDecoder() throws AudioDecodeException {
		this.checkClosed();

		if (this.decoderHandler != NULL)
			return;

		this.openDecoder();
	}

	private void ensureMetadata() throws AudioDecodeException {
		if (this.metadata != null)
			return;

		this.ensureDecoder();

		try (MemoryStack stack = MemoryStack.stackPush()) {
			STBVorbisComment commentInfo = STBVorbisComment.malloc(stack);
			stb_vorbis_get_comment(this.decoderHandler, commentInfo);

			Map<String, String> comments = new HashMap<>();

			int commentCount = commentInfo.comment_list_length();
			PointerBuffer commentListBuf = commentInfo.comment_list();
			for (int i = 0; i < commentCount; i++) {
				String commentKV = memUTF8(commentListBuf.get(i));

				int splitIndex = commentKV.indexOf('=');
				if (splitIndex == -1) {
					comments.put(commentKV, "");
				} else {
					String key = commentKV.substring(0, splitIndex);
					String value = commentKV.substring(splitIndex + 1);
					comments.put(key, value);
				}
			}

			this.metadata = new SimpleAudioMetadata(
				this.frameRate,
				this.channels,
				this.totalFrames,
				comments
			);
		}
	}

	@Override
	protected boolean isSupportHint(@NotNull AudioDecodeHint hint) {
		return false;
	}

	@NotNull
	@Override
	public AudioMetadata getMetadata() throws AudioDecodeException {
		this.ensureMetadata();
		return this.metadata;
	}

	@Override
	public int decode(@NotNull FloatBuffer buffer, int maxFrame) throws AudioDecodeException {
		Objects.requireNonNull(buffer, "buffer is null");

		this.ensureDecoder();

		int maxSamples = maxFrame * this.channels;
		if (maxSamples > buffer.remaining())
			throw new IndexOutOfBoundsException("Float buffer cannot accommodate " + maxSamples + ", capacity=" + buffer.capacity());

		float[] temp = new float[maxSamples];
		int consumedFrame = stb_vorbis_get_samples_float_interleaved(this.decoderHandler, this.channels, temp);
		if (consumedFrame == 0)
			return -1;

		buffer.put(temp, 0, consumedFrame * this.channels);

		return consumedFrame;
	}

	@Override
	public void close() throws IOException {
		if (this.closed)
			return;
		this.closed = true;

		if (this.decoderHandler != NULL)
			stb_vorbis_close(this.decoderHandler);

		if (this.dataBuffer != null)
			memFree(this.dataBuffer);
	}
}
