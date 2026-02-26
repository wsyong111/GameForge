package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.ogg;

import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioCategory;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.lwjgl.stb.STBVorbisInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OggMetadata implements AudioMetadata {
	private final int sampleRate;
	private final int channels;
	private final int bitDepth;
	private final long totalSamples;
	private final boolean streamable;
	private final boolean seekable;

	private final Map<Key<?>, String> comments;
	private final Map<Key<?>, Object> parsedComments;

	public OggMetadata(int sampleRate, int channels, int bitDepth, long totalSamples, boolean streamable, boolean seekable, @NotNull Map<Key<?>, String> comments) {
		Objects.requireNonNull(comments, "comments is null");

		this.sampleRate = sampleRate;
		this.channels = channels;
		this.bitDepth = bitDepth;
		this.totalSamples = totalSamples;
		this.streamable = streamable;
		this.seekable = seekable;
		this.comments = Map.copyOf(comments);

		this.parsedComments = new ConcurrentHashMap<>();
	}

	@Override
	public int getSampleRate() {
		return this.sampleRate;
	}

	@Override
	public int getChannels() {
		return this.channels;
	}

	@Override
	public int getBitDepth() {
		return this.bitDepth;
	}

	@Override
	public long getTotalSamples() {
		return this.totalSamples;
	}

	@Override
	public boolean isStreamable() {
		return this.streamable;
	}

	@Override
	public boolean isSeekable() {
		return this.seekable;
	}

	@Override
	public @NotNull AudioCategory getCategory() {
		return null;
	}

	@Nullable
	@Override
	public String getRaw(@NotNull Key<?> key) {
		Objects.requireNonNull(key, "key is null");
		return this.comments.get(key);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T> T get(@NotNull Key<T> key) {
		Objects.requireNonNull(key, "key is null");

		return (T) this.parsedComments.computeIfAbsent(key, k ->
			key.getParser().apply(this.getRaw(key)));
	}

	@NotNull
	@UnmodifiableView
	@Override
	public Set<Key<?>> getKeys() {
		return this.comments.keySet();
	}
}
