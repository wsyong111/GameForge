package io.github.wsyong11.gameforge.framework.system.resource.v2;

import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Objects;

public abstract class CombinedResource implements MultiSourceResource {
	private final List<Resource> resources;
	private final List<ResourcePack> sources;

	protected CombinedResource(@NotNull List<Resource> resources) {
		Objects.requireNonNull(resources, "resources is null");
		this.resources = List.copyOf(resources);

		this.sources = this.resources
			.stream()
			.map(Resource::getSource)
			.toList();
	}

	@NotNull
	@UnmodifiableView
	protected List<Resource> getResources() {
		return this.resources;
	}

	@NotNull
	@UnmodifiableView
	@Override
	public List<ResourcePack> getSourceList() {
		return this.sources;
	}

	@NotNull
	@UnmodifiableView
	@Override
	public List<Resource> getCandidates() {
		return this.resources;
	}

	@Nullable
	@Override
	public ResourcePack getSource() {
		return this.sources.isEmpty() ? null : this.sources.get(0);
	}
}
