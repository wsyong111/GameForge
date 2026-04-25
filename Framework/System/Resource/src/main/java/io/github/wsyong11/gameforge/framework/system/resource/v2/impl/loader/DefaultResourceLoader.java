package io.github.wsyong11.gameforge.framework.system.resource.v2.impl.loader;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.impl.graph.SimpleResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.impl.transform.ResourceGraphTransformer;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceConflictResolver;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import io.github.wsyong11.gameforge.util.concurrent.signal.ThreadSignal;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CancellationException;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public class DefaultResourceLoader extends ResourceLoader {
	private static final Logger LOGGER = Log.getLogger();

	private final List<ResourcePack> packs;
	private final List<ResourceConflictResolver> conflictResolvers;
	private final List<ResourceTransformer> transformers;

	private final Scheduler packReloadScheduler;
	private final int packLoadConcurrent;
	private final Scheduler transformScheduler;
	private final int transformConcurrent;

	public DefaultResourceLoader(
		@NotNull List<ResourcePack> packs,
		@NotNull List<ResourceConflictResolver> conflictResolvers,
		@NotNull List<ResourceTransformer> transformers,
		@NotNull Scheduler packReloadScheduler,
		int packLoadConcurrent,
		@NotNull Scheduler transformScheduler,
		int transformConcurrent
	) {
		Objects.requireNonNull(packs, "packs is null");
		Objects.requireNonNull(conflictResolvers, "conflictResolvers is null");
		Objects.requireNonNull(transformers, "transformers is null");
		Objects.requireNonNull(packReloadScheduler, "packReloadScheduler is null");
		Objects.requireNonNull(transformScheduler, "transformScheduler is null");

		if (packLoadConcurrent <= 0)
			throw new IllegalArgumentException("Pack load concurrent count cannot be less than one");

		if (transformConcurrent <= 0)
			throw new IllegalArgumentException("Transform concurrent count cannot be less than one");

		this.packs = List.copyOf(packs);
		this.conflictResolvers = List.copyOf(conflictResolvers);
		this.transformers = List.copyOf(transformers);

		this.packReloadScheduler = packReloadScheduler;
		this.packLoadConcurrent = packLoadConcurrent;
		this.transformScheduler = transformScheduler;
		this.transformConcurrent = transformConcurrent;
	}

	@NotNull
	protected List<Resource> loadResourcePack(@NotNull ResourcePack pack) throws IOException {
		Objects.requireNonNull(pack, "pack is null");

		Thread thread = Thread.currentThread();
		if (thread.isInterrupted())
			throw new CancellationException();

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
		for (ResourcePath path : pack.list()) {
			if (thread.isInterrupted())
				throw new CancellationException();

			list.add(new PackResource(pack, path));
		}

		return Collections.unmodifiableList(list);
	}

	@NotNull
	protected Map<ResourcePath, List<Resource>> loadAndListResources(@NotNull List<ResourcePack> packs) {
		Objects.requireNonNull(packs, "packs is null");

		ThreadSignal completed = new ThreadSignal();
		Map<ResourcePath, List<Resource>> result = new HashMap<>();

		Disposable disposable = Flowable
			.fromIterable(packs)
			.flatMap(
				pack -> Flowable
					.fromCallable(() -> this.loadResourcePack(pack))
					.subscribeOn(this.packReloadScheduler)
					.onErrorResumeNext(e -> {
						if (e instanceof CancellationException)
							return Flowable.error(e);

						LOGGER.warn("Resource pack load fail", e);
						return Flowable.empty();
					}),
				this.packLoadConcurrent
			)
			.flatMapIterable(l -> l)
			.buffer(128)
			.observeOn(Schedulers.single())
			.doOnComplete(completed::set)
			.subscribe(resources -> {
				for (Resource resource : resources) {
					ResourcePath path = resource.getPath();
					result.computeIfAbsent(path, k -> new ArrayList<>())
					      .add(resource);
				}
			});

		try {
			completed.await();
		} catch (InterruptedException e) {
			disposable.dispose();
			Thread.currentThread().interrupt();
			throw new CancellationException();
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

	@NotNull
	protected ResourceGraph transformResources(@NotNull ResourceGraph graph, @NotNull List<ResourceTransformer> transformers) {
		Objects.requireNonNull(graph, "graph is null");
		Objects.requireNonNull(transformers, "transformers is null");

		try {
			ResourceGraphTransformer transformer = new ResourceGraphTransformer(transformers, this.transformScheduler, this.transformConcurrent);
			return transformer.transform(graph);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new CancellationException();
		} catch (Exception e) {
			LOGGER.error("Failed to transform resource", e);
			throw e;
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
		Map<ResourcePath, List<Resource>> resourceMap = this.loadAndListResources(this.packs);

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
		ResourceGraph graph = new SimpleResourceGraph();
		for (Resource resource : resources)
			graph.put(resource.getPath(), resource);

		LOGGER.debug("Transforming resources...");
		ResourceGraph transformedGraph = this.transformResources(graph, this.transformers);

		LOGGER.debug("Transform complete, total {} resources", transformedGraph.size());

		return transformedGraph;
	}
}
