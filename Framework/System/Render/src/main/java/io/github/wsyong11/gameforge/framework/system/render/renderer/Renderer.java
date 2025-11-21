package io.github.wsyong11.gameforge.framework.system.render.renderer;

import io.github.wsyong11.gameforge.framework.system.render.context.RenderContext;
import org.jetbrains.annotations.NotNull;

public interface Renderer {
	void init(@NotNull RendererContext context);

	void render(@NotNull RendererContext rendererContext, @NotNull RenderContext context);

	default boolean shouldRender() {
		return true;
	}

	default boolean isDirty() {
		return true;
	}
}
