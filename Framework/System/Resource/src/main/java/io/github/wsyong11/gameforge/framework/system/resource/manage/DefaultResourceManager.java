package io.github.wsyong11.gameforge.framework.system.resource.manage;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;
import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceConflictHandler;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.listener.ReloadListener;
import io.github.wsyong11.gameforge.framework.system.resource.pack.DirResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.pack.ZipResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultResourceManager implements ResourceManager {
	private static final Logger LOGGER = Log.getLogger();

	private final ResourcePath basePath;

	private final ListenerList listenerList;

	private final List<ResourcePack> resourcePacks;
	private final Map<Identifier, ResourceConflictHandler> conflictHandlerMap;
	private Map<Identifier, Resource> resourceMap;

	public DefaultResourceManager(@NotNull ResourcePath basePath) {
		Objects.requireNonNull(basePath, "basePath is null");
		this.basePath = basePath;

		this.listenerList = ListenerList.sync();

		this.resourcePacks = new CopyOnWriteArrayList<>();
		this.conflictHandlerMap = new ConcurrentHashMap<>();
		this.resourceMap = Map.of();
	}

	@Override
	public void setConflictHandler(@NotNull Identifier id, @Nullable ResourceConflictHandler handler) {
		Objects.requireNonNull(id, "id is null");

		if (handler == null)
			this.conflictHandlerMap.remove(id);
		else
			this.conflictHandlerMap.put(id, handler);
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
		Map<Identifier, ResourceConflictHandler> conflictHandlerMap = Map.copyOf(this.conflictHandlerMap);

		LOGGER.debug("Reloading resource manager");
		long startTimeNs = System.nanoTime();

		this.listenerList.fire(ReloadListener.class,
			ReloadListener::onPreReload,
			ListenerExceptionCallback.log(LOGGER));

		Map<Identifier, List<Resource>> candidates = new HashMap<>();
		int totalResources = 0;

		for (ResourcePack pack : packs) {
			if (pack.isClosed()) {
				LOGGER.warn("Resource pack {} is closed, skipped", pack);
				continue;
			}

			String id = pack.getId();
			ResourcePath packPath = this.basePath.resolve(id);

			for (ResourcePath fullPath : pack.listResources()) {
				if (!fullPath.startsWith(packPath))
					continue;

				ResourcePath resourcePath = fullPath.relativize(packPath, true);
				Identifier location = Identifier.of(id, resourcePath.toString());

				Resource resource = pack.getResource(fullPath);
				if (resource == null) {
					LOGGER.debug("Cannot get resource {} from resource pack {}", fullPath, pack);
					continue;
				}

				candidates.computeIfAbsent(location, k -> new ArrayList<>()).add(resource);
				totalResources++;
			}
		}

		LOGGER.debug("Scan complete, {} resources found", totalResources);

		// Process conflict and generate a final resource map
		Map<Identifier, Resource> resourceMap = new HashMap<>();
		for (var entry : candidates.entrySet()) {
			Identifier location = entry.getKey();
			List<Resource> resources = entry.getValue();

			if (resources.size() == 1) {
				resourceMap.put(location, resources.get(0));
				continue;
			}

			ResourceConflictHandler handler = conflictHandlerMap.get(location);

			if (handler != null) {
				try {
					Resource resolved = handler.processConflict(resources);
					if (resolved != null)
						resourceMap.put(location, resolved);
				} catch (Exception e) {
					LOGGER.error("Conflict handler failed for {}", location, e);
				}
				continue;
			}

			Resource resource = resources.get(0);
			resourceMap.put(location, resource);
			if (LOGGER.isTraceEnabled())
				LOGGER.trace("Conflict on {}: using first resource by default: {}", location, resource);
		}

		LOGGER.debug("Process resources complete, total {} resources key", resourceMap.size());

		this.resourceMap = Collections.unmodifiableMap(resourceMap);

		this.listenerList.fire(ReloadListener.class,
			ReloadListener::onReload,
			ListenerExceptionCallback.log(LOGGER));

		long tookTimeNs = System.nanoTime() - startTimeNs;
		LOGGER.debug("Reload complete took {} ms", tookTimeNs / 1000L / 1000L);
	}

	@Override
	public void unregisterReloadListener(@NotNull ReloadListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.remove(ReloadListener.class, listener);
	}

	@Override
	public void registerReloadListener(@NotNull ReloadListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.add(ReloadListener.class, listener);
	}

	@Nullable
	@Override
	public Resource getResource(@NotNull Identifier name) {
		Objects.requireNonNull(name, "name is null");
		return this.resourceMap.get(name);
	}
}
