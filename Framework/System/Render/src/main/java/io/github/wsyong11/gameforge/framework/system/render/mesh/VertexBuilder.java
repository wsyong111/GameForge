package io.github.wsyong11.gameforge.framework.system.render.mesh;

import io.github.wsyong11.gameforge.framework.Color;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import java.util.Objects;

public interface VertexBuilder {
	@NotNull
	default VertexBuilder color(@NotNull Vector4fc color) {
		Objects.requireNonNull(color, "color is null");
		return this.color(color.x(), color.y(), color.z(), color.w());
	}

	@NotNull
	default VertexBuilder color(float r, float g, float b, float a) {
		return this.attribute("color", r, g, b, a);
	}

	@NotNull
	default VertexBuilder color(@NotNull Vector3fc color) {
		Objects.requireNonNull(color, "color is null");
		return this.color(color.x(), color.y(), color.z());
	}

	@NotNull
	default VertexBuilder color(float r, float g, float b) {
		return this.color(r, g, b, 1.0F);
	}

	@NotNull
	default VertexBuilder color(@NotNull Color color) {
		return this.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	default VertexBuilder uv(float u, float v) {
		return this.attribute("uv", u, v);
	}

	@NotNull
	default VertexBuilder uv(@NotNull Vector2fc uv) {
		Objects.requireNonNull(uv, "uv is null");
		return this.uv(uv.x(), uv.y());
	}

	@NotNull
	default VertexBuilder uv2(float u, float v) {
		return this.attribute("uv2", u, v);
	}

	@NotNull
	default VertexBuilder uv2(@NotNull Vector2fc uv) {
		Objects.requireNonNull(uv, "uv is null");
		return this.uv2(uv.x(), uv.y());
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	default VertexBuilder normal(float x, float y, float z) {
		return this.attribute("normal", x, y, z);
	}

	@NotNull
	default VertexBuilder normal(@NotNull Vector3fc normal) {
		Objects.requireNonNull(normal, "normal is null");
		return this.normal(normal.x(), normal.y(), normal.z());
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	VertexBuilder attribute(@NotNull String name, float... values);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	MeshBuilder end();
}
