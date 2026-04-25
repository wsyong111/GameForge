package io.github.wsyong11.gameforge.framework.system.resource.v2.impl.pack;

import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import io.github.wsyong11.gameforge.util.IdentityRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class ResourcePackRegistry {
	private final List<IdentityRef<ResourcePack>> packs;
	private final Map<IdentityRef<ResourcePack>, Integer> packPriority;
	private final Object packListLock;

	private volatile List<ResourcePack> currentPacks;

	public ResourcePackRegistry() {
		this.packs = new ArrayList<>();
		this.packPriority = new HashMap<>();
		this.packListLock = new Object();

		this.currentPacks = List.of();
	}

	public void update() {
		List<IdentityRef<ResourcePack>> packs;
		Map<IdentityRef<ResourcePack>, Integer> packPriority;

		synchronized (this.packListLock) {
			packs = List.copyOf(this.packs);
			packPriority = Map.copyOf(this.packPriority);
		}

		this.currentPacks = packs
			.stream()
			.sorted(Comparator
				.comparingInt(pack -> packPriority.getOrDefault(pack, 0)))
			.map(IdentityRef::get)
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
		List<IdentityRef<ResourcePack>> packs;
		synchronized (this.packListLock) {
			packs = List.copyOf(this.packs);
		}

		return packs
			.stream()
			.map(IdentityRef::get)
			.toList();
	}

	public void setPriority(@NotNull ResourcePack pack, int priority) {
		Objects.requireNonNull(pack, "pack is null");

		IdentityRef<ResourcePack> ref = new IdentityRef<>(pack);

		synchronized (this.packListLock) {
			if (!this.packs.contains(ref))
				throw new IllegalArgumentException("Resource pack is not register");

			this.packPriority.put(ref, priority);
		}
	}

	public int getPriority(@NotNull ResourcePack pack) {
		Objects.requireNonNull(pack, "pack is null");

		IdentityRef<ResourcePack> ref = new IdentityRef<>(pack);

		synchronized (this.packListLock) {
			if (!this.packs.contains(ref))
				throw new IllegalArgumentException("Resource pack not register");

			return this.packPriority.getOrDefault(ref, 0);
		}
	}

	public boolean register(@NotNull ResourcePack pack) {
		Objects.requireNonNull(pack, "pack is null");

		IdentityRef<ResourcePack> ref = new IdentityRef<>(pack);
		synchronized (this.packListLock) {
			if (this.packs.contains(ref))
				return false;

			this.packs.add(ref);
			return true;
		}
	}

	public boolean unregister(@NotNull ResourcePack pack) {
		Objects.requireNonNull(pack, "pack is null");

		IdentityRef<ResourcePack> ref = new IdentityRef<>(pack);
		synchronized (this.packListLock) {
			if (!this.packs.remove(ref))
				return false;

			this.packPriority.remove(ref);
			return true;
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
