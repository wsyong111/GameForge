package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.simple;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ResourcePackRegistry {
	private final List<ResourcePack> packs;
	private final Map<ResourcePack, Integer> packPriority;
	private final Object packListLock;

	private volatile List<ResourcePack> currentPacks;

	public ResourcePackRegistry() {
		this.packs = new ArrayList<>();
		this.packPriority = new IdentityHashMap<>();
		this.packListLock = new Object();

		this.currentPacks = List.of();
	}

	public void update() {
		List<ResourcePack> packs;
		Map<ResourcePack, Integer> packPriority;

		synchronized (this.packListLock) {
			packs = List.copyOf(this.packs);
			packPriority = Map.copyOf(this.packPriority);
		}

		this.currentPacks = packs
			.stream()
			.sorted(Comparator.comparingInt(
				pack -> packPriority.getOrDefault(pack, 0)))
			.toList();
	}

	@NotNull
	@UnmodifiableView
	public List<ResourcePack> getCurrentPacks() {
		return this.currentPacks;
	}

	@NotNull
	@Unmodifiable
	public List<ResourcePack> getPacks() {
		return List.copyOf(this.packs);
	}

	public void setPriority(@NotNull ResourcePack pack, int priority) {
		Objects.requireNonNull(pack, "pack is null");

		synchronized (this.packListLock) {
			if (!this.packs.contains(pack))
				throw new IllegalArgumentException("Resource pack not register");

			this.packPriority.put(pack, priority);
		}
	}

	public int getPriority(@NotNull ResourcePack pack) {
		Objects.requireNonNull(pack, "pack is null");

		synchronized (this.packListLock) {
			if (!this.packs.contains(pack))
				throw new IllegalArgumentException("Resource pack not register");

			return this.packPriority.getOrDefault(pack, 0);
		}
	}

	public void register(@NotNull ResourcePack pack) {
		Objects.requireNonNull(pack, "pack is null");

		synchronized (this.packListLock) {
			this.packs.add(pack);
		}
	}

	public void unregister(@NotNull ResourcePack pack) {
		Objects.requireNonNull(pack, "pack is null");

		synchronized (this.packListLock) {
			this.packs.remove(pack);
			this.packPriority.remove(pack);
		}
	}

	public void clear() {
		synchronized (this.packListLock) {
			this.packs.clear();
			this.packPriority.clear();
		}
	}

	public void clearAll() {
		this.clear();
		this.currentPacks = List.of();
	}
}
