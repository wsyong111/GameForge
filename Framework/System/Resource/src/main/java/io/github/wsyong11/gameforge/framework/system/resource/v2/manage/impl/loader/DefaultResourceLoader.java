package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.loader;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceConflictResolver;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.SimpleResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public class DefaultResourceLoader extends ResourceLoader {
	private static final Logger LOGGER = Log.getLogger();

	private final List<ResourcePack> packs;
	private final List<ResourceConflictResolver> conflictResolvers;
	private final List<ResourceTransformer> transformers;
	private final ExecutorService packReloadPool;

	public DefaultResourceLoader(
		@NotNull List<ResourcePack> packs,
		@NotNull List<ResourceConflictResolver> conflictResolvers,
		@NotNull List<ResourceTransformer> transformers,
		@NotNull ExecutorService packReloadPool
	) {
		Objects.requireNonNull(packs, "packs is null");
		Objects.requireNonNull(conflictResolvers, "conflictResolvers is null");
		Objects.requireNonNull(transformers, "transformers is null");
		Objects.requireNonNull(packReloadPool, "packReloadPool is null");

		this.packs = List.copyOf(packs);
		this.conflictResolvers = List.copyOf(conflictResolvers);
		this.transformers = List.copyOf(transformers);
		this.packReloadPool = packReloadPool;
	}

	@NotNull
	protected List<Resource> reloadResourcePack(@NotNull ResourcePack pack) throws IOException {
		Objects.requireNonNull(pack, "pack is null");

		LOGGER.debug("Loading resource pack {}", pack);

		pack.load(new ResourcePack.LoadListener() {
			@Override
			public void onStart(int total) {

			}

			@Override
			public void onSuccess(@NotNull ResourcePath path) {

			}

			@Override
			public void onFailure(@NotNull ResourcePath path, @NotNull Throwable e) {

			}

			@Override
			public void onComplete() {

			}
		});

		List<Resource> list = new ArrayList<>();
		for (ResourcePath path : pack.list())
			list.add(new PackResource(pack, path));
		return Collections.unmodifiableList(list);
	}

	@NotNull
	protected Map<ResourcePath, List<Resource>> reloadAndListResources(@NotNull List<ResourcePack> packs) {
		Objects.requireNonNull(packs, "packs is null");

		List<Future<List<Resource>>> reloadFutures = packs
			.stream()
			.map(pack ->
				this.packReloadPool.submit(() ->
					this.reloadResourcePack(pack)))
			.toList();

		Map<ResourcePath, List<Resource>> result = new HashMap<>();
		for (Future<List<Resource>> future : reloadFutures) {
			List<Resource> futureResult;
			try {
				futureResult = future.get();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				for (Future<List<Resource>> f : reloadFutures)
					f.cancel(true);

				throw new CancellationException();
			} catch (ExecutionException e) {
				LOGGER.warn("Resource pack reload fail", e.getCause());
				continue;
			}

			for (Resource resource : futureResult) {
				ResourcePath path = resource.getPath();
				result.computeIfAbsent(path, k -> new ArrayList<>())
				      .add(resource);
			}
		}

		return Collections.unmodifiableMap(result);
	}

	@NotNull
	private List<Resource> resolveConflict(@NotNull List<ResourceConflictResolver> resolvers, @NotNull Map<ResourcePath, List<Resource>> resources) {
		Objects.requireNonNull(resolvers, "resolvers is null");
		Objects.requireNonNull(resources, "resources is null");

		List<Resource> result = new ArrayList<>();
		for (Map.Entry<ResourcePath, List<Resource>> entry : resources.entrySet()) {
			ResourcePath path = entry.getKey();

			List<Resource> resourceList = entry.getValue();
			if (resourceList.size() == 1) {
				result.add(resourceList.get(0));
				continue;
			}

			LOGGER.trace("Resolving resource {}", path);

			Resource resolvedResource = null;
			for (ResourceConflictResolver resolver : resolvers) {
				try {
					if (!resolver.isSupportPath(path))
						continue;
				} catch (Exception e) {
					LOGGER.error("Uncaught exception while checking support in {}", resolver, e);
					continue;
				}

				try {
					resolvedResource = resolver.resolve(path, Collections.unmodifiableList(resourceList));
				} catch (Exception e) {
					LOGGER.error("Uncaught exception while resolving resource {} in {}", path, resolver, e);
					continue;
				}

				if (resolvedResource != null)
					break;
			}

			result.add(resolvedResource == null ? resourceList.get(0) : resolvedResource);
		}

		return Collections.unmodifiableList(result);
	}

	protected void transformResources(@NotNull ResourceGraph graph, @NotNull List<ResourceTransformer> transformers) {
		Objects.requireNonNull(graph, "graph is null");

		for (ResourceTransformer transformer : transformers) {

		}
	}

	@NotNull
	@Override
	protected ResourceGraph doLoad() {
		LOGGER.debug("Pack info list:{}", lazy(() -> {
			StringBuilder sb = new StringBuilder();
			for (ResourcePack pack : this.packs) {
				sb.append('\n');
				sb.append("| ");
				sb.append(pack.getClass().getName());
				sb.append(": \"");
				sb.append(pack.getSource());
				sb.append('"');
			}
			return sb.toString();
		}));

		LOGGER.debug("Reloading resource packs...");
		Map<ResourcePath, List<Resource>> resourceMap = this.reloadAndListResources(this.packs);

		LOGGER.debug("Pack load completed, total {} paths, {} resources in {} packs",
			resourceMap.size(),
			lazy(() -> resourceMap
				.values()
				.stream()
				.mapToInt(List::size)
				.sum()),
			this.packs.size());

		LOGGER.debug("Resolving resources...");
		List<Resource> resources = this.resolveConflict(this.conflictResolvers, resourceMap);

		LOGGER.debug("Resolve completed, total {} resources", resources.size());

		LOGGER.debug("Building resource graph...");
		SimpleResourceGraph graph = new SimpleResourceGraph();
		for (Resource resource : resources)
			graph.put(resource.getPath(), resource);

		LOGGER.debug("Transforming resources...");
		this.transformResources(graph, this.transformers);

		return graph.freeze();
	}

}
