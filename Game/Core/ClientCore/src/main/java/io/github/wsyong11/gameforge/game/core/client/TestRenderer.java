package io.github.wsyong11.gameforge.game.core.client;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.render.context.RenderContext;
import io.github.wsyong11.gameforge.framework.system.render.mesh.Mesh;
import io.github.wsyong11.gameforge.framework.system.render.renderer.RendererContext;
import io.github.wsyong11.gameforge.framework.system.render.renderer.StaticRenderer;
import org.jetbrains.annotations.NotNull;

public class TestRenderer extends StaticRenderer {
	private static final Identifier SHADER = Identifier.withDefaultNamespace("test");

	private Mesh testMesh;

	public TestRenderer() {
		this.testMesh = null;
	}

	@Override
	public void init(@NotNull RendererContext context) {
		context.preloadShader(SHADER);
		this.testMesh = context
			.mesh()
			.vertex(-0.5F, -0.5F, 0.0F).color(1.0F, 0.0F, 0.0F).end() // 声明顶点
			.vertex(0.5F, -0.5F, 0.0F).color(0.0F, 1.0F, 0.0F).end() // 声明顶点
			.vertex(0.0F, 0.5F, 0.0F).color(0.0F, 0.0F, 1.0F).end() // 声明顶点
			.build();
	}

	@Override
	public void render(@NotNull RendererContext rendererContext, @NotNull RenderContext context) {
		super.render(rendererContext, context);

		context.push() // 入栈
		       .shader(SHADER) // 使用着色器
		       .mesh(this.testMesh)
		       .draw() // 绘制上次的Mesh
		       .pop(); // 出栈
	}
}
