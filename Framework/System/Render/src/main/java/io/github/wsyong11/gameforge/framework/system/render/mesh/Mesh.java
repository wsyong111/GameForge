package io.github.wsyong11.gameforge.framework.system.render.mesh;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public interface Mesh extends AutoCloseable {
	@Contract("_, _, -> param2")
	@NotNull
	Vector3f getVertex(int index, @NotNull Vector3f dest);

	@NotNull
	default Vector3f getVertex(int index) {
		return this.getVertex(index, new Vector3f());
	}

	int getVertexCount();

//
//	@NotNull
//	FaceAttribute getVertex(int index);

	@Override
	void close();
}
