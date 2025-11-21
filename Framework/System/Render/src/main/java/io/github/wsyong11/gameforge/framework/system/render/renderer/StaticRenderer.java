package io.github.wsyong11.gameforge.framework.system.render.renderer;

import io.github.wsyong11.gameforge.framework.system.render.context.RenderContext;
import org.jetbrains.annotations.NotNull;

public abstract class StaticRenderer implements Renderer {
	private boolean dirty;

	public StaticRenderer() {
		this.dirty = true;
	}

	@Override
	public void render(@NotNull RendererContext rendererContext, @NotNull RenderContext context) {
		this.dirty = false;
	}

	@Override
	public boolean isDirty() {
		return this.dirty;
	}
}
