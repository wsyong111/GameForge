package io.github.wsyong11.gameforge.framework.system.render.context;

import io.github.wsyong11.gameforge.framework.Color;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

public interface VertexBuilder {
	@NotNull
	VertexBuilder color(@NotNull Vector4fc color);

	@NotNull
	VertexBuilder color(float r, float g, float b, float a);

	@NotNull
	VertexBuilder color(@NotNull Vector3fc color);

	@NotNull
	VertexBuilder color(float r, float g, float b);

	@NotNull
	VertexBuilder color(@NotNull Color color);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	VertexBuilder uv(float u, float v);

	@NotNull
	VertexBuilder uv(@NotNull Vector2fc uv);

	@NotNull
	VertexBuilder uv2(float u, float v);

	@NotNull
	VertexBuilder uv2(@NotNull Vector2fc uv);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	VertexBuilder normal(float x, float y, float z);

	@NotNull
	VertexBuilder normal(@NotNull Vector3fc normal);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	VertexBuilder attribute(@NotNull String name, float... values);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	RenderContext end();
}
