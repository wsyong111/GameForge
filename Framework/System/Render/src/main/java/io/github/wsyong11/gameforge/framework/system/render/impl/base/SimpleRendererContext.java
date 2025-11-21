package io.github.wsyong11.gameforge.framework.system.render.impl.base;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.render.mesh.MeshBuilder;
import io.github.wsyong11.gameforge.framework.system.render.renderer.RendererContext;
import org.jetbrains.annotations.NotNull;

public class SimpleRendererContext implements RendererContext {
	@NotNull
	@Override
	public MeshBuilder mesh() {
		return null;
	}

	@Override
	public void preloadShader(Identifier shader) {

	}
}
