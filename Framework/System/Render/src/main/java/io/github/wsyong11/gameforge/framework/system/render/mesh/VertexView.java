package io.github.wsyong11.gameforge.framework.system.render.mesh;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;

public interface VertexView {
	@NotNull
	Mesh getMesh();

	int getVertexIndex();

	@NotNull
	default Vector3f getVertex() {
		return this.getVertex(new Vector3f());
	}

	@NotNull
	@Contract("_ -> param1")
	Vector3f getVertex(@NotNull Vector3f dest);

	@NotNull
	default Vector2f getUV() {
		return this.getUV(new Vector2f());
	}

	@NotNull
	@Contract("_ -> param1")
	Vector2f getUV(@NotNull Vector2f dest);

	@NotNull
	default Vector3f getNormal() {
		return this.getNormal(new Vector3f());
	}

	@NotNull
	@Contract("_ -> param1")
	Vector3f getNormal(@NotNull Vector3f dest);
}
