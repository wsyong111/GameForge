package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.transform;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.query.ResourceQuery;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.TransformContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.UnaryOperator;

/*
1. 添加或修改的资源不会被重复变换
2. 移除的资源在未被变换前会被跳过
3. 替换会在结尾并行运行，若返回空则删除资源并中断变换链
 */
public final class GraphTransformContext implements TransformContext {
	private final ResourceGraph graph;

	private final Set<ResourcePath> removedResources;
	private final Map<ResourcePath, Operate> operates;

	public GraphTransformContext(@NotNull ResourceGraph graph) {
		Objects.requireNonNull(graph, "graph is null");
		this.graph = graph;

		this.removedResources = new HashSet<>();
		this.operates = new HashMap<>();
	}

	@Override
	public void replaceResource(@NotNull ResourcePath path, @NotNull UnaryOperator<Resource> transformer) {
		Objects.requireNonNull(path, "path is null");
		Objects.requireNonNull(transformer, "transformer is null");
		this.operates.put(path, new ReplaceOperate(transformer));
	}

	@Override
	public void addResource(@NotNull ResourcePath path, @NotNull Resource resource) {
		Objects.requireNonNull(path, "path is null");
		Objects.requireNonNull(resource, "resource is null");
		this.operates.put(path, new AddOperate(resource));
	}

	@Override
	public void removeResource(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		this.operates.put(path, RemoveOperate.INSTANCE);
	}

	@Nullable
	@Override
	public Resource getResource(@NotNull ResourcePath path) {
		return null;
	}

	@NotNull
	@Override
	public ResourceQuery query() {
		// TODO: 2026/4/19 Filter removed resource
		return this.graph.query();
	}

	public interface Operate {
		void apply(@NotNull ResourcePath path, @NotNull ResourceGraph graph);
	}

	public static class RemoveOperate implements Operate {
		public static final RemoveOperate INSTANCE = new RemoveOperate();

		@Override
		public void apply(@NotNull ResourcePath path, @NotNull ResourceGraph graph) {
			Objects.requireNonNull(path, "path is null");
			Objects.requireNonNull(graph, "graph is null");
			graph.remove(path);
		}
	}

	public static class AddOperate implements Operate {
		private final Resource resource;

		public AddOperate(@NotNull Resource resource) {
			Objects.requireNonNull(resource, "resource is null");
			this.resource = resource;
		}

		@NotNull
		public Resource getResource() {
			return this.resource;
		}

		@Override
		public void apply(@NotNull ResourcePath path, @NotNull ResourceGraph graph) {
			Objects.requireNonNull(path, "path is null");
			Objects.requireNonNull(graph, "graph is null");
			graph.put(path, this.resource);
		}
	}

	public static class ReplaceOperate implements Operate {
		private final UnaryOperator<Resource> transformer;

		public ReplaceOperate(@NotNull UnaryOperator<Resource> transformer) {
			Objects.requireNonNull(transformer, "transformer is null");
			this.transformer = transformer;
		}

		@NotNull
		public UnaryOperator<Resource> getTransformer() {
			return this.transformer;
		}

		@Override
		public void apply(@NotNull ResourcePath path, @NotNull ResourceGraph graph) {
			Objects.requireNonNull(path, "path is null");
			Objects.requireNonNull(graph, "graph is null");

			Resource resource = graph.get(path);
			Resource transformedResource = this.transformer.apply(resource);
			if (transformedResource == null)
				graph.remove(path);
			else
				graph.put(path, transformedResource);
		}
	}
}
