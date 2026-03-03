package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class AudioDecoderMap {
	private final List<AudioDecoderFactory> list;
	private final Map<AudioDecoderFactory, FactoryInfo> infoMap;
	private final Map<MimeType, List<FactoryInfo>> cacheMap;

	private final Object lock;

	public AudioDecoderMap() {
		this.list = new ArrayList<>();

		this.infoMap = new HashMap<>();
		this.cacheMap = new HashMap<>();

		this.lock = new Object();
	}

	@Nullable
	@Unmodifiable
	public List<FactoryInfo> get(@NotNull MimeType mimeType) {
		Objects.requireNonNull(mimeType, "mimeType is null");

		List<FactoryInfo> cacheList = this.cacheMap.get(mimeType);
		if (cacheList != null)
			return cacheList;

		synchronized (this.lock) {
			List<FactoryInfo> list = this.find(mimeType);
			this.cacheMap.putIfAbsent(mimeType, list);
			return list;
		}
	}

	private List<FactoryInfo> find(@NotNull MimeType mimeType) {

	}

	public void register(@NotNull AudioDecoderFactory factory) {
		Objects.requireNonNull(factory, "factory is null");


		this.list.add(factory);
	}

	public void unregister(@NotNull AudioDecoderFactory factory) {
		Objects.requireNonNull(factory, "factory is null");

		this.list.remove(factory);
	}

	public static class FactoryInfo {

	}
}
