package io.github.wsyong11.gameforge.framework.system.render.context;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.render.mesh.Mesh;
import org.jetbrains.annotations.NotNull;

/**
 * 渲染上下文对象，用于上传和绘制顶点数据。
 *
 * <p>该对象通常由渲染管线提供，包含顶点构建器、实例化构建器以及
 * 变换堆栈等接口。</p>
 *
 * <p><b>注意：</b>
 * <ul>
 *   <li>请勿保存或缓存 RenderContext 实例。</li>
 *   <li>RenderContext 的生命周期由渲染管线管理。</li>
 *   <li>必须在渲染线程中使用，否则行为未定义。</li>
 * </ul>
 * </p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * context.shader(shaderId)
 *        .beginMesh()
 *        .vertex(0, 0, 0).color(1, 0, 0, 1).end()
 *        .vertex(1, 0, 0).color(0, 1, 0, 1).end()
 *        .endMesh()
 *        .draw();
 *
 * // 实例化渲染
 * context.shader(shaderId)
 *        .beginMesh()
 *        .vertex(0, 0, 0).color(1, 0, 0, 1).end()
 *        .vertex(1, 0, 0).color(0, 1, 0, 1).end()
 *        .endMesh()
 *        .instance().scale(2).end()
 *        .draw();
 * }</pre>
 *
 * @implNote RenderContext 通常是即时模式 API，调用顺序必须严格遵循。
 * 错误的 begin/end 调用可能导致异常或未定义行为。
 */
public interface RenderContext {
	/**
	 * 设定当前渲染使用的着色器。
	 *
	 * <p>后续上传的顶点数据与实例化数据都会使用此着色器。
	 *
	 * @param id 已加载的着色器标识符
	 * @return 当前上下文对象，用于链式调用
	 * @throws IllegalArgumentException 当指定的着色器不存在时抛出
	 */
	@NotNull
	RenderContext shader(@NotNull Identifier id);

	// -------------------------------------------------------------------------------------------------------------- //
//
//	/**
//	 * 开始定义一个新的网格。
//	 *
//	 * <p>在 {@link #endMesh()} 调用之前，所有通过
//	 * {@link #vertex(double, double, double)} 添加的顶点都会属于该网格。</p>
//	 *
//	 * @return 当前上下文对象，用于链式调用
//	 * @throws IllegalStateException 在上一个网格尚未结束时抛出
//	 */
//	@NotNull
//	RenderContext beginMesh();
//
//	/**
//	 * 结束当前网格定义。
//	 *
//	 * <p>结束后，网格会被缓存，直到调用 {@link #beginMesh()} 开始新的定义。
//	 * 在调用 {@link #draw()} 前可以重复绑定不同的实例化数据。</p>
//	 *
//	 * @return 当前上下文对象，用于链式调用
//	 * @throws IllegalStateException 在没有开始网格定义时调用抛出
//	 */
//	@NotNull
//	RenderContext endMesh();

//	/**
//	 * 创建一个顶点构建器对象。
//	 *
//	 * <p>顶点的基础位置会自动应用 {@link #poseStack()} 中的变换矩阵。</p>
//	 *
//	 * @param x 顶点在模型空间中的 X 坐标
//	 * @param y 顶点在模型空间中的 Y 坐标
//	 * @param z 顶点在模型空间中的 Z 坐标
//	 * @return 顶点构建器对象
//	 * @apiNote 根据后端实现不同，可能会返回复用对象。
//	 * 请勿将构建器保存到字段或长期变量中，否则可能导致错误或未定义行为。
//	 * @see VertexBuilder
//	 */
//	@NotNull
//	VertexBuilder vertex(double x, double y, double z);

	@NotNull
	RenderContext mesh(@NotNull Mesh mesh);

	/**
	 * 获取实例化构建器，用于为当前网格添加实例化渲染参数。
	 *
	 * <p>实例化数据包括：平移、缩放、旋转以及用户自定义属性。
	 *
	 * @return 实例化构建器
	 */
	@NotNull
	InstanceBuilder instance();

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 获取当前的变换堆栈。
	 *
	 * <p>变换堆栈用于管理模型矩阵（例如平移、旋转、缩放）。
	 * 顶点数据在提交时会应用该矩阵。</p>
	 *
	 * @return 变换堆栈对象
	 */
	@NotNull
	PoseStack poseStack();

	/**
	 * 压栈当前上下文状态。
	 *
	 * <p>新的状态会继承上层状态（如矩阵、着色器等），
	 * 可以在此基础上进行修改。</p>
	 *
	 * @return 当前上下文对象，用于链式调用
	 */
	@NotNull
	RenderContext push();

	/**
	 * 弹出当前上下文状态。
	 *
	 * <p>恢复为上一次 push 的状态。</p>
	 *
	 * @return 当前上下文对象，用于链式调用
	 * @throws IllegalStateException 没有更多上下文状态时抛出
	 */
	@NotNull
	RenderContext pop();

	/**
	 * 绘制最近缓存的网格。
	 *
	 * <p>该方法会将网格与实例化数据提交给 GPU，并添加到命令队列中。</p>
	 *
	 * @return 当前上下文对象，用于链式调用
	 * @throws IllegalStateException 如果没有可用的网格数据抛出
	 */
	@NotNull
	RenderContext draw();
}
