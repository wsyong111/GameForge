package io.github.wsyong11.gameforge.framework.system.resource.v2.impl.transform;

import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ex.TransformResourceException;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.query.ResourceQuery;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.TransformContext;
import io.github.wsyong11.gameforge.util.concurrent.signal.ThreadSignal;
import io.github.wsyong11.gameforge.util.exception.ExceptionUtils;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class ResourceGraphTransformer {
	private final List<ResourceTransformer> transformers;
	private final Scheduler scheduler;
	private final int concurrent;

	public ResourceGraphTransformer(
		@NotNull List<ResourceTransformer> transformers,
		@NotNull Scheduler scheduler,
		int concurrent
	) {
		Objects.requireNonNull(transformers, "transformers is null");
		Objects.requireNonNull(scheduler, "scheduler is null");

		if (concurrent <= 0)
			throw new IllegalArgumentException("Concurrent count cannot be less than one");

		this.transformers = transformers;
		this.scheduler = scheduler;
		this.concurrent = concurrent;
	}

	private void transformOne(
		@NotNull ResourceGraph graph,
		@NotNull ResourceTransformer transformer,
		@NotNull Map<ResourcePath, List<UnaryOperator<Resource>>> resourceTransformers
	) {
		Objects.requireNonNull(graph, "graph is null");
		Objects.requireNonNull(transformer, "transformer is null");
		Objects.requireNonNull(resourceTransformers, "resourceTransformers is null");

		Context ctx = new Context(graph);

		Map<ResourcePath, ResourceOperate> operates = new LinkedHashMap<>();

		Deque<ResourcePath> pathStack = new ArrayDeque<>();
		pathStack.push(ResourcePath.ROOT);

		while (!pathStack.isEmpty()) {
			ResourcePath current = pathStack.pop();

			if (graph.isDir(current)) {
				List<ResourcePath> paths = graph.list(current);
				if (paths == null)
					continue;

				int size = paths.size();
				for (int i = size - 1; i >= 0; i--)
					pathStack.push(paths.get(i));
				continue;
			}

			assert graph.isEntry(current);

			if (operates.containsKey(current))
				continue;

			Resource resource = graph.get(current);
			if (resource == null)
				continue;

			ctx.reset();
			try {
				transformer.transform(resource, ctx);
			} catch (Exception e) {
				throw new TransformResourceException(
					"Failed to transform resource " + resource.getPath() +
					" using transformer " + transformer, e);
			}
			operates.putAll(ctx.getOperateMap());
		}

		for (Map.Entry<ResourcePath, ResourceOperate> entry : operates.entrySet()) {
			ResourcePath path = entry.getKey().toFile();
			ResourceOperate operate = entry.getValue();

			if (operate instanceof ResourceOperate.Remove) {
				graph.remove(path);
			} else if (operate instanceof ResourceOperate.Add opAdd) {
				graph.put(path, opAdd.getResource());
			} else if (operate instanceof ResourceOperate.Replace opReplace) {
				resourceTransformers
					.computeIfAbsent(path, k -> new ArrayList<>())
					.add(opReplace.getReplacer());
			} else {
				throw new UnsupportedOperationException("Unknown operate type " + operate);
			}
		}
	}

	@NotNull
	public ResourceGraph transform(@NotNull ResourceGraph graph) throws InterruptedException {
		Objects.requireNonNull(graph, "graph is null");

		ResourceGraph transformedGraph = graph.copy();

		Map<ResourcePath, List<UnaryOperator<Resource>>> resourceTransformers = new LinkedHashMap<>();
		for (ResourceTransformer transformer : this.transformers)
			this.transformOne(transformedGraph, transformer, resourceTransformers);

		this.transformResource(transformedGraph, resourceTransformers);

		return transformedGraph;
	}

	private void transformResource(@NotNull ResourceGraph graph, @NotNull Map<ResourcePath, List<UnaryOperator<Resource>>> resourceTransformers) throws InterruptedException {
		Objects.requireNonNull(graph, "graph is null");
		Objects.requireNonNull(resourceTransformers, "resourceTransformers is null");

		ThreadSignal completed = new ThreadSignal();
		AtomicReference<Throwable> exception = new AtomicReference<>(null);

		Disposable disposable = Flowable
			.fromIterable(resourceTransformers.entrySet())
			.map(entry -> {
				ResourcePath path = entry.getKey();
				return Triple.of(
					path,
					entry.getValue(),
					graph.get(path)
				);
			})
			.filter(item -> item.getRight() != null)
			.flatMap(
				item -> Flowable
					.fromCallable(() -> {
						Resource result = this.transformResourceAsync(item.getRight(), item.getMiddle());
						return Pair.of(item.getLeft(), result);
					})
					.onErrorResumeNext(e -> Flowable
						.error(new TransformResourceException("Failed replace resource " + item.getLeft(), e)))
					.subscribeOn(this.scheduler),
				this.concurrent
			)
			.toList()
			.observeOn(Schedulers.single())
			.subscribe(
				result -> {
					for (Pair<ResourcePath, Resource> item : result) {
						ResourcePath path = item.getKey();
						Resource value = item.getValue();

						if (value != null)
							graph.put(path, value);
						else
							graph.remove(path);
					}
					completed.set();
				},
				e -> {
					exception.set(e);
					completed.set();
				}
			);

		try {
			completed.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			disposable.dispose();
			throw e;
		}

		Throwable ex = exception.get();
		if (ex != null)
			throw ExceptionUtils.wrap(ex, TransformResourceException.class, TransformResourceException::new);
	}

	@Nullable
	private Resource transformResourceAsync(@NotNull Resource resource, @NotNull List<UnaryOperator<Resource>> transformers) {
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
		private final ResourceGraph graph;
		private final Map<ResourcePath, ResourceOperate> operateMap;

		private Context(@NotNull ResourceGraph graph) {
			Objects.requireNonNull(graph, "graph is null");

			this.graph = graph;

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

		public void reset() {
			this.operateMap.clear();
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
