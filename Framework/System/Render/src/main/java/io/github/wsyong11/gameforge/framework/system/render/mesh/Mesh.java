package io.github.wsyong11.gameforge.framework.system.render.mesh;

import org.jetbrains.annotations.NotNull;

public interface Mesh extends AutoCloseable {
	int getVertexCount();

	@NotNull
	VertexAttribute getVertex(int index);

	@Override
	void close();
}
