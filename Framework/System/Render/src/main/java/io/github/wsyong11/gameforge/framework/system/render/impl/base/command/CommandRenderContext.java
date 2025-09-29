package io.github.wsyong11.gameforge.framework.system.render.impl.base.command;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.render.context.InstanceBuilder;
import io.github.wsyong11.gameforge.framework.system.render.context.PoseStack;
import io.github.wsyong11.gameforge.framework.system.render.context.RenderContext;
import io.github.wsyong11.gameforge.framework.system.render.context.VertexBuilder;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.SimplePoseStack;
import org.jetbrains.annotations.NotNull;

public class CommandRenderContext implements RenderContext {
	public CommandRenderContext(RenderCommandStack commandStack, SimplePoseStack poseStack) {
	}

	@Override
	public @NotNull RenderContext shader(@NotNull Identifier id) {
		return null;
	}

	@Override
	public @NotNull RenderContext beginMesh() {
		return null;
	}

	@Override
	public @NotNull RenderContext endMesh() {
		return null;
	}

	@Override
	public @NotNull VertexBuilder vertex(double x, double y, double z) {
		return null;
	}

	@Override
	public @NotNull InstanceBuilder instance() {
		return null;
	}

	@Override
	public @NotNull PoseStack poseStack() {
		return null;
	}

	@Override
	public @NotNull RenderContext push() {
		return null;
	}

	@Override
	public @NotNull RenderContext pop() {
		return null;
	}

	@Override
	public @NotNull RenderContext draw() {
		return null;
	}
}
