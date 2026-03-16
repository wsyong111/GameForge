package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder;

import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioCategory;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SimpleAudioMetadata implements AudioMetadata {
	private final int sampleRate;
	private final int channels;
	private final long totalSamples;

	private final Map<String, String> comments;
	private final Map<Key<?>, Object> parsedCommentCache;

	public SimpleAudioMetadata(
		int sampleRate,
		int channels,
		long totalSamples,
		@NotNull Map<String, String> comments
	) {
		Objects.requireNonNull(comments, "comments is null");

		this.sampleRate = sampleRate;
		this.channels = channels;
		this.totalSamples = totalSamples;
		this.comments = Map.copyOf(comments);

		this.parsedCommentCache = new ConcurrentHashMap<>();
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
	public long getTotalFrames() {
		return this.totalSamples;
	}

	@NotNull
	@Override
	public AudioCategory getCategory() {
		return null;
	}

	@Nullable
	@Override
	public String getRaw(@NotNull Key<?> key) {
		Objects.requireNonNull(key, "key is null");
		return this.comments.get(key.getKey());
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T> T get(@NotNull Key<T> key) {
		Objects.requireNonNull(key, "key is null");

		return (T) this.parsedCommentCache.computeIfAbsent(key, k ->
			k.getParser().apply(this.getRaw(key)));
	}

	@NotNull
	@UnmodifiableView
	@Override
	public Set<String> getKeys() {
		return this.comments.keySet();
	}

	@Override
	public String toString() {
		String comments = this.comments
			.entrySet()
			.stream()
			.map(e -> e.getKey() + "=\"" + StringEscapeUtils.escapeJava(e.getValue()) + "\"")
			.collect(Collectors.joining(", "));

		return "AudioMetadata{"
			+ "sampleRate=" + (this.sampleRate / 1000) + "K, "
			+ "channels=" + this.channels + ", "
			+ "totalSamples=" + this.totalSamples + ", "
			+ "comments={" + comments + "}}";
	}
}
