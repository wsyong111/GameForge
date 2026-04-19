package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.loader;

import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ResourceGraphTransformer {
	private final ResourceGraph graph;
	private final List<ResourceTransformer> transformers;

	private final AtomicBoolean transformed;

	public ResourceGraphTransformer(@NotNull ResourceGraph graph, @NotNull List<ResourceTransformer> transformers) {
		Objects.requireNonNull(graph, "graph is null");
		Objects.requireNonNull(transformers, "transformers is null");

		this.graph = graph;
		this.transformers = transformers;

		this.transformed = new AtomicBoolean(false);
	}

	@NotNull
	public ResourceGraph transform() {
		if (!this.transformed.compareAndSet(false, true))
			throw new IllegalStateException("Transform cannot be repeated");


	}
}
