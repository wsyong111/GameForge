package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.loader;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.SimpleResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;

public class ResourceGraphTransformer {
	private final ResourceGraph graph;
	private final List<ResourceTransformer> transformers;
	private final ExecutorService transformExecutor;

	private final Map<ResourcePath, List<UnaryOperator<Resource>>> replaceTransformers;

	private final AtomicBoolean transformed;

	public ResourceGraphTransformer(
		@NotNull ResourceGraph graph,
		@NotNull List<ResourceTransformer> transformers,
		@NotNull ExecutorService transformExecutor
	) {
		Objects.requireNonNull(graph, "graph is null");
		Objects.requireNonNull(transformers, "transformers is null");
		Objects.requireNonNull(transformExecutor, "transformExecutor is null");

		this.graph = graph;
		this.transformers = transformers;
		this.transformExecutor = transformExecutor;

		this.replaceTransformers = new LinkedHashMap<>();

		this.transformed = new AtomicBoolean(false);
	}

	private void transformOne(@NotNull ResourceGraph resultGraph, @NotNull ResourceTransformer transformer) {
		Objects.requireNonNull(resultGraph, "resultGraph is null");
		Objects.requireNonNull(transformer, "transformer is null");


	}

	@NotNull
	public ResourceGraph transform() {
		if (!this.transformed.compareAndSet(false, true))
			throw new IllegalStateException("Transform cannot be repeated");

		ResourceGraph resultGraph = new SimpleResourceGraph();

		for (ResourceTransformer transformer : this.transformers)
			this.transformOne(resultGraph, transformer);

		this.replaceResource(resultGraph);

		return resultGraph;
	}

	private void replaceResource(@NotNull ResourceGraph resultGraph) {
		Objects.requireNonNull(resultGraph, "resultGraph is null");

//		Map<ResourcePath, Future<Resource>> futures = new HashMap<>();
//
//		for (Map.Entry<ResourcePath, List<UnaryOperator<Resource>>> entry : this.replaceTransformers.entrySet()) {
//			ResourcePath path = entry.getKey();
//			List<UnaryOperator<Resource>> transformers = entry.getValue();
//
//			Resource resource = resultGraph.get(path);
//			if (resource == null)
//				continue;
//
//			Future<Resource> future = this.transformExecutor.submit(() -> this.replaceAsync(path, resource, transformers));
//			futures.put(path, future);
//		}
//
//		for (Map.Entry<ResourcePath, Future<Resource>> entry : futures.entrySet()) {
//			ResourcePath path = entry.getKey();
//			Future<Resource> future = entry.getValue();
//
//		}
	}

	@Nullable
	private Resource replaceAsync(@NotNull ResourcePath path, @NotNull Resource resource, @NotNull List<UnaryOperator<Resource>> transformers) {
		Objects.requireNonNull(path, "path is null");
		Objects.requireNonNull(resource, "resource is null");
		Objects.requireNonNull(transformers, "transformers is null");

		Resource currentResource = resource;
		for (UnaryOperator<Resource> transformer : transformers) {
			currentResource = transformer.apply(currentResource);
			if (currentResource == null)
				return null;
		}

		return currentResource;
	}
}
