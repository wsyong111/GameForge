package io.github.wsyong11.gameforge.framework.system.render;

import io.github.wsyong11.gameforge.framework.Identifier;
import org.jetbrains.annotations.NotNull;

public interface RenderContext {
	@NotNull
	RenderContext shader(@NotNull Identifier id);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	VertexBuilder vertex(float x, float y, float z);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	PoseStack poseStack();

	@NotNull
	RenderContext push();

	@NotNull
	RenderContext pop();
}
