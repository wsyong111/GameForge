package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.transform;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class TransformOperate {
	@NotNull
	public static RemoveOperate remove() {
		return RemoveOperate.INSTANCE;
	}

	@NotNull
	public static AddOperate add(@NotNull Resource resource) {
		Objects.requireNonNull(resource, "resource is null");
		return new AddOperate(resource);
	}

	public abstract void apply(@NotNull ResourcePath path, @NotNull ResourceGraph graph);

	public static class RemoveOperate extends TransformOperate {
		public static final RemoveOperate INSTANCE = new RemoveOperate();

		@Override
		public void apply(@NotNull ResourcePath path, @NotNull ResourceGraph graph) {
			Objects.requireNonNull(path, "path is null");
			Objects.requireNonNull(graph, "graph is null");
			graph.remove(path);
		}
	}

	public static class AddOperate extends TransformOperate {
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

			assert this.resource.getPath().equals(path);
			graph.put(path, this.resource);
		}
	}
}
