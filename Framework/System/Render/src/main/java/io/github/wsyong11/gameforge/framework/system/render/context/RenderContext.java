package io.github.wsyong11.gameforge.framework.system.render.context;

import io.github.wsyong11.gameforge.framework.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 渲染上下文对象，用于上传和绘制顶点数据。
 *
 * <p>该对象通常由渲染管线提供，包含顶点构建器、实例化构建器以及
 * 变换堆栈等接口。</p>
 *
 * <p><b>注意：</b>请勿保存或缓存 RenderContext 实例。
 * 它的生命周期由渲染管线管理，错误的持有可能导致内存泄漏或未定义行为。</p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * context.shader(shaderId)
 *        .beginMesh()
 *        .vertex(0, 0, 0).color(1, 0, 0, 1).end()
 *        .vertex(1, 0, 0).color(0, 1, 0, 1).end()
 *        .endMesh()
 *        .draw();
 * }</pre>
 */
public interface RenderContext {
	/**
	 * 设定绘制的着色器。
	 *
	 * <p>后续上传的顶点数据与实例化数据都会使用此着色器。</p>
	 *
	 * @param id 已加载的着色器标识符
	 * @return 当前上下文对象，用于链式调用
	 */
	@NotNull
	RenderContext shader(@NotNull Identifier id);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	RenderContext beginMesh();

	@NotNull
	RenderContext endMesh();

	/**
	 * 创建一个顶点构建器对象。
	 *
	 * <p>顶点的基础位置会自动应用 {@link #poseStack()} 中的变换矩阵。</p>
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
	InstanceBuilder instance();

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 获取当前的变换堆栈。
	 *
	 * <p>变换堆栈用于管理模型矩阵（例如平移、旋转、缩放）。</p>
	 *
	 * @return 变换堆栈对象
	 */
	@NotNull
	PoseStack poseStack();

	@NotNull
	RenderContext push();

	@NotNull
	RenderContext pop();

	@NotNull
	RenderContext draw();
}
