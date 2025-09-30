package io.github.wsyong11.gameforge.framework.system.render.impl.base.command;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.render.Renderer;
import io.github.wsyong11.gameforge.framework.system.render.context.InstanceBuilder;
import io.github.wsyong11.gameforge.framework.system.render.context.PoseStack;
import io.github.wsyong11.gameforge.framework.system.render.context.RenderContext;
import io.github.wsyong11.gameforge.framework.system.render.context.VertexBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

public class CommandRenderContext implements RenderContext {
	private static final Logger LOGGER = Log.getLogger();

	private final PoseStack poseStack;

	private final Deque<Context> stateStack;
	private Context state;

	private Renderer currentRenderer;

	public CommandRenderContext(@NotNull RenderCommandStack commandStack, @NotNull PoseStack poseStack) {
		Objects.requireNonNull(poseStack, "poseStack is null");

		this.poseStack = poseStack;

		this.stateStack = new LinkedList<>();
		this.state = null;

		this.currentRenderer = null;

		this.reset();
	}

	protected void reset() {
		this.stateStack.clear();
		this.state = null;

		this.push();
	}

	public void beginRender(@NotNull Renderer renderer) {
		Objects.requireNonNull(renderer, "renderer is null");

		if (this.currentRenderer != null)
			throw new IllegalStateException("This context is in rendering " + this.currentRenderer);

		this.currentRenderer = renderer;
	}

	public void endRender(@Nullable Renderer renderer) {
		if (this.currentRenderer == null)
			throw new IllegalStateException("This context is not in rendering");

		if (renderer != null && this.currentRenderer != renderer)
			throw new IllegalArgumentException("Inviable renderer " + renderer);

		if (LOGGER.isTraceEnabled() && this.stateStack.size() != 1)
			LOGGER.trace("State stack is not full clean when rendering {}", renderer);

		this.currentRenderer = null;

		this.reset();
	}

	@NotNull
	@Override
	public RenderContext push() {
		Context context = new Context();
		if (this.state != null)
			this.state.copyTo(context);

		this.stateStack.push(context);
		this.state = context;

		return this;
	}

	@NotNull
	@Override
	public RenderContext pop() {
		if (this.stateStack.size() == 1)
			throw new IllegalStateException("Top level context cannot pop");

		this.stateStack.pop();
		this.state = this.stateStack.peek();

		return this;
	}

	@NotNull
	@Override
	public RenderContext shader(@NotNull Identifier id) {
		Objects.requireNonNull(id, "id is null");
		this.state.setShader(id);
		return this;
	}

	@NotNull
	@Override
	public RenderContext beginMesh() {
		return null;
	}

	@NotNull
	@Override
	public RenderContext endMesh() {
		return null;
	}

	@NotNull
	@Override
	public VertexBuilder vertex(double x, double y, double z) {
		return null;
	}

	@NotNull
	@Override
	public InstanceBuilder instance() {
		return null;
	}

	@NotNull
	@Override
	public PoseStack poseStack() {
		return this.poseStack;
	}

	@NotNull
	@Override
	public RenderContext draw() {
		return null;
	}

	protected static class Context {
		private Identifier shader;

		protected Context() {
			this.shader = null;
		}

		public void setShader(@Nullable Identifier shader) {
			this.shader = shader;
		}

		@Nullable
		public Identifier getShader() {
			return this.shader;
		}

		public void copyTo(@NotNull Context context) {
			Objects.requireNonNull(context, "context is null");

			context.setShader(this.getShader());
		}
	}
}
