package io.github.wsyong11.gameforge.framework.system.render.mesh;

import io.github.wsyong11.gameforge.framework.system.render.mesh.vertex.VertexBuilder;
import org.jetbrains.annotations.NotNull;

public interface MeshBuilder {
	/**
	 * 创建一个顶点构建器对象。
	 *
	 * @param x 顶点在模型空间中的 X 坐标
	 * @param y 顶点在模型空间中的 Y 坐标
	 * @param z 顶点在模型空间中的 Z 坐标
	 * @return 顶点构建器对象
	 * @apiNote 根据后端实现不同，可能会返回复用对象。
	 * 请勿将构建器保存到字段或长期变量中，否则可能导致错误或未定义行为。
	 * @see VertexBuilder
	 */
	@NotNull
	VertexBuilder vertex(double x, double y, double z);

	@NotNull
	Mesh build();
}
