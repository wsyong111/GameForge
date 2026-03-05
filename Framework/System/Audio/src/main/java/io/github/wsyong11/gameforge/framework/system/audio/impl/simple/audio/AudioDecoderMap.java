package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoderFactory;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AudioDecoderMap {
	private static final Logger LOGGER = Log.getLogger();

	private final List<AudioDecoderFactory> list;
	private final Map<AudioDecoderFactory, FactoryInfo> infoMap;
	private final Map<MimeType, List<FactoryInfo>> cacheMap;

	private final Object lock;

	public AudioDecoderMap() {
		this.list = new ArrayList<>();

		this.infoMap = new IdentityHashMap<>();
		this.cacheMap = new ConcurrentHashMap<>();

		this.lock = new Object();
	}

	@NotNull
	@Unmodifiable
	public List<FactoryInfo> get(@NotNull MimeType mimeType) {
		Objects.requireNonNull(mimeType, "mimeType is null");

		return this.cacheMap.computeIfAbsent(mimeType, this::find);
	}

	@NotNull
	private List<FactoryInfo> find(@NotNull MimeType mimeType) {
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

	public void register(@NotNull AudioDecoderFactory factory) {
		Objects.requireNonNull(factory, "factory is null");

		synchronized (this.lock) {
			if (this.list.contains(factory))
				return;

			Set<MimeType> supportMimes = factory.getSupportMimes();
			Set<AudioDecodeHint> supportHints = factory.getSupportHints();

			FactoryInfo info = new FactoryInfo(factory, supportMimes, supportHints);

			this.infoMap.put(factory, info);
			this.list.add(factory);

			this.cacheMap.clear();
		}
	}

	public void unregister(@NotNull AudioDecoderFactory factory) {
		Objects.requireNonNull(factory, "factory is null");

		synchronized (this.lock) {
			if (!this.list.remove(factory))
				return;

			this.infoMap.remove(factory);

			this.cacheMap.clear();
		}
	}

	public void clear() {
		synchronized (this.lock){
			this.list.clear();
			this.infoMap.clear();
			this.cacheMap.clear();
		}
	}

	public static class FactoryInfo {
		private final AudioDecoderFactory factory;
		private final Set<MimeType> supportMimes;
		private final Set<AudioDecodeHint> supportHints;

		public FactoryInfo(@NotNull AudioDecoderFactory factory, @NotNull Set<MimeType> supportMimes, @NotNull Set<AudioDecodeHint> supportHints) {
			Objects.requireNonNull(factory, "factory is null");
			Objects.requireNonNull(supportMimes, "supportMimes is null");
			Objects.requireNonNull(supportHints, "supportHints is null");

			this.factory = factory;
			this.supportMimes = Set.copyOf(supportMimes);
			this.supportHints = Set.copyOf(supportHints);
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
	}
}
