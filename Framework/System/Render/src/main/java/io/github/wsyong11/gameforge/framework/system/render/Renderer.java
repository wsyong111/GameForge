package io.github.wsyong11.gameforge.framework.system.render;

import io.github.wsyong11.gameforge.framework.system.render.context.RenderContext;
import org.jetbrains.annotations.NotNull;

public interface Renderer {
	void render(@NotNull RenderContext context);

	boolean shouldRender();

	boolean isDirty();
}
