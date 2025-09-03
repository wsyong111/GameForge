package io.github.wsyong11.gameforge.framework.system.resource.manage;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceConflictHandler;
import io.github.wsyong11.gameforge.framework.system.resource.listener.ReloadListener;
import io.github.wsyong11.gameforge.framework.system.resource.pack.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultResourceManager implements ResourceManager {
	private static final Logger LOGGER = Log.getLogger();

	private final String basePath;

	private final ListenerList listenerList;

	private final List<ResourcePack> resourcePacks;
	private Map<Identifier, Resource> resourceMap;

	public DefaultResourceManager(@NotNull String basePath) {
		Objects.requireNonNull(basePath, "basePath is null");
		this.basePath = basePath;

		this.listenerList = ListenerList.sync();

		this.resourcePacks = new CopyOnWriteArrayList<>();
		this.resourceMap = Map.of();
	}

	@Override
	public void unregisterConflictHandler(@NotNull String path) {

	}

	@Override
	public void registerConflictHandler(@NotNull String path, @NotNull ResourceConflictHandler handler) {

	}

	@Override
	public void addPack(int index, @NotNull ResourcePack pack) {
		Objects.requireNonNull(pack, "pack is null");
		this.resourcePacks.add(index, pack);
	}

	@Override
	public void removePack(@NotNull ResourcePack pack) {
		Objects.requireNonNull(pack, "pack is null");
		this.resourcePacks.remove(pack);
	}

	@NotNull
	@UnmodifiableView
	@Override
	public List<ResourcePack> getResourcePacks() {
		return Collections.unmodifiableList(this.resourcePacks);
	}

	@Override
	public void reload() {
		List<ResourcePack> packs = List.copyOf(this.resourcePacks);

		this.listenerList.fire(ReloadListener.class,
			ReloadListener::onPreReload,
			ListenerExceptionCallback.log(LOGGER));

		Map<Identifier, Resource> resourceMap = new HashMap<>();

		ListIterator<ResourcePack> it = packs.listIterator(packs.size());
		while (it.hasPrevious()) {
			ResourcePack pack = it.previous();
			String id = pack.getId();

			for (String path : pack.listResources()) {
				Identifier location = Identifier.of(id, path);
				resourceMap.putIfAbsent(location, pack.getResource(path));
			}
		}

		this.resourceMap = Collections.unmodifiableMap(resourceMap);

		this.listenerList.fire(ReloadListener.class,
			ReloadListener::onReload,
			ListenerExceptionCallback.log(LOGGER));
	}

	@Override
	public void unregisterReloadListener(@NotNull ReloadListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.add(ReloadListener.class, listener);
	}

	@Override
	public void registerReloadListener(@NotNull ReloadListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.remove(ReloadListener.class, listener);
	}

	@Nullable
	@Override
	public Resource getResource(@NotNull Identifier name) {
		Objects.requireNonNull(name, "name is null");
		return this.resourceMap.get(name);
	}
}
