package io.github.wsyong11.gameforge.framework.system.render;

import org.jetbrains.annotations.NotNull;

public interface VertexBuilder {
	@NotNull
	VertexBuilder color(float r, float g, float b, float a);
}
