package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio.decode;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToLongFunction;

public class AudioDecoderRegistry {
	private final List<AudioDecoderFactory> list;
	private final Map<AudioDecoderFactory, FactoryInfo> infoMap;

	private final Map<MimeType, List<FactoryInfo>> cacheMap;

	private final Object lock;

	public AudioDecoderRegistry() {
		this.list = new ArrayList<>();

		this.infoMap = new IdentityHashMap<>();
		this.cacheMap = new ConcurrentHashMap<>();

		this.lock = new Object();
	}

	@NotNull
	@Unmodifiable
	public List<FactoryInfo> get(@NotNull MimeType mimeType, @NotNull Set<AudioDecodeHint> hints) {
		Objects.requireNonNull(mimeType, "mimeType is null");
		Objects.requireNonNull(hints, "hints is null");

		return this
			.findRoughCached(mimeType)
			.stream()
			.sorted(Comparator
				.comparingLong((ToLongFunction<FactoryInfo>) i -> this.calculateScore(i, mimeType, hints))
				.reversed())
			.toList();
	}

	protected long calculateScore(@NotNull FactoryInfo info, @NotNull MimeType mimeType, @NotNull Set<AudioDecodeHint> hints) {
		Objects.requireNonNull(info, "info is null");
		Objects.requireNonNull(mimeType, "mimeType is null");
		Objects.requireNonNull(hints, "hints is null");

		long score = 0L;

		score += info
			.getSupportMimes()
			.stream()
			.mapToInt(e -> e.distance(mimeType))
			.max()
			.orElse(0) * 100L;

		score += info
			.getSupportHints()
			.stream()
			.filter(hints::contains)
			.count() * 10;

		score += info.getPriority();

		return score;
	}

	@NotNull
	@Unmodifiable
	protected List<FactoryInfo> findRough(@NotNull MimeType mimeType) {
		Objects.requireNonNull(mimeType, "mimeType is null");

		List<AudioDecoderFactory> list;
		Map<AudioDecoderFactory, FactoryInfo> infoMap;
		synchronized (this.lock) {
			list = List.copyOf(this.list);
			infoMap = Map.copyOf(this.infoMap);
		}

		return list
			.stream()
			.map(infoMap::get)
			.filter(Objects::nonNull)
			.filter(info -> info
				.getSupportMimes()
				.stream()
				.anyMatch(mime -> mime.includes(mimeType)))
			.toList();
	}

	@NotNull
	@Unmodifiable
	protected List<FactoryInfo> findRoughCached(@NotNull MimeType mimeType) {
		Objects.requireNonNull(mimeType, "mimeType is null");
		return this.cacheMap.computeIfAbsent(mimeType, this::findRough);
	}

	public void clearCache() {
		this.cacheMap.clear();
	}

	public boolean register(@NotNull AudioDecoderFactory factory, int priority) {
		Objects.requireNonNull(factory, "factory is null");

		synchronized (this.lock) {
			if (this.list.contains(factory))
				return false;

			Set<MimeType> supportMimes = factory.getSupportMimes();
			Set<AudioDecodeHint> supportHints = factory.getSupportHints();

			FactoryInfo info = new FactoryInfo(factory, supportMimes, supportHints, priority);

			this.infoMap.put(factory, info);
			this.list.add(factory);

			this.clearCache();
		}
		return true;
	}

	public boolean unregister(@NotNull AudioDecoderFactory factory) {
		Objects.requireNonNull(factory, "factory is null");

		synchronized (this.lock) {
			if (!this.list.remove(factory))
				return false;

			this.infoMap.remove(factory);

			this.clearCache();
		}
		return true;
	}

	public void clear() {
		synchronized (this.lock) {
			this.list.clear();
			this.infoMap.clear();
			this.cacheMap.clear();
		}
	}

	public static class FactoryInfo {
		private final AudioDecoderFactory factory;
		private final Set<MimeType> supportMimes;
		private final Set<AudioDecodeHint> supportHints;
		private final int priority;

		public FactoryInfo(
			@NotNull AudioDecoderFactory factory,
			@NotNull Set<MimeType> supportMimes,
			@NotNull Set<AudioDecodeHint> supportHints,
			int priority
		) {
			Objects.requireNonNull(factory, "factory is null");
			Objects.requireNonNull(supportMimes, "supportMimes is null");
			Objects.requireNonNull(supportHints, "supportHints is null");

			this.factory = factory;
			this.supportMimes = Set.copyOf(supportMimes);
			this.supportHints = Set.copyOf(supportHints);
			this.priority = priority;
		}

		@NotNull
		public AudioDecoderFactory getFactory() {
			return this.factory;
		}

		@NotNull
		@UnmodifiableView
		public Set<MimeType> getSupportMimes() {
			return this.supportMimes;
		}

		@NotNull
		@UnmodifiableView
		public Set<AudioDecodeHint> getSupportHints() {
			return this.supportHints;
		}

		public int getPriority() {
			return this.priority;
		}
	}
}
