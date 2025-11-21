package io.github.wsyong11.gameforge.framework.system.render.renderer;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.render.mesh.MeshBuilder;
import org.jetbrains.annotations.NotNull;

public interface RendererContext {
	@NotNull
	MeshBuilder mesh();

	void preloadShader(Identifier shader);
}
