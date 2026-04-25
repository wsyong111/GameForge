package io.github.wsyong11.gameforge.framework.system.resource.v2;

import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface MultiSourceResource extends Resource {
	@NotNull
	@UnmodifiableView
	List<ResourcePack> getSourceList();

	@NotNull
	@UnmodifiableView
	List<Resource> getCandidates();
}
