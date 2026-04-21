package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.loader;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.graph.SimpleResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.query.ResourceQuery;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.TransformContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ExecutorService;
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

		Context context = new Context(this.graph);
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

	protected static class Context implements TransformContext {
		private final ResourceGraph currentGraph;
		private final Map<ResourcePath, ResourceOperate> operateMap;

		private Context(@NotNull ResourceGraph currentGraph) {
			Objects.requireNonNull(currentGraph, "graph is null");

			this.currentGraph = currentGraph;

			this.operateMap = new HashMap<>();
		}

		@Override
		public void replaceResource(@NotNull ResourcePath path, @NotNull UnaryOperator<Resource> transformer) {
			Objects.requireNonNull(path, "path is null");
			Objects.requireNonNull(transformer, "transformer is null");
			this.operateMap.put(path.toFile(), ResourceOperate.replace(transformer));
		}

		@Override
		public void addResource(@NotNull ResourcePath path, @NotNull Resource resource) {
			Objects.requireNonNull(path, "path is null");
			Objects.requireNonNull(resource, "resource is null");
			this.operateMap.put(path.toFile(), ResourceOperate.add(resource));
		}

		@Override
		public void removeResource(@NotNull ResourcePath path) {
			Objects.requireNonNull(path, "path is null");
			this.operateMap.put(path.toFile(), ResourceOperate.remove());
		}

		@Nullable
		@Override
		public Resource getResource(@NotNull ResourcePath path) {
			Objects.requireNonNull(path, "path is null");
			return this.graph.get(path);
		}

		@NotNull
		@Override
		public ResourceQuery query() {
			return this.graph.query();
		}

		@NotNull
		@Unmodifiable
		public Map<ResourcePath, ResourceOperate> getOperateMap() {
			return Map.copyOf(this.operateMap);
		}
	}

	protected interface ResourceOperate {
		@NotNull
		static Remove remove() {
			return Remove.INSTANCE;
		}

		@NotNull
		static Add add(@NotNull Resource resource) {
			Objects.requireNonNull(resource, "resource is null");
			return new Add(resource);
		}

		@NotNull
		static Replace replace(@NotNull UnaryOperator<Resource> replacer) {
			Objects.requireNonNull(replacer, "replacer is null");
			return new Replace(replacer);
		}

		class Remove implements ResourceOperate {
			public static final Remove INSTANCE = new Remove();
		}

		class Add implements ResourceOperate {
			private final Resource resource;

			public Add(@NotNull Resource resource) {
				Objects.requireNonNull(resource, "resource is null");
				this.resource = resource;
			}

			@NotNull
			public Resource getResource() {
				return this.resource;
			}
		}

		class Replace implements ResourceOperate {
			private final UnaryOperator<Resource> replacer;

			public Replace(@NotNull UnaryOperator<Resource> replacer) {
				Objects.requireNonNull(replacer, "replacer is null");
				this.replacer = replacer;
			}

			@NotNull
			public UnaryOperator<Resource> getReplacer() {
				return this.replacer;
			}
		}
	}
}
