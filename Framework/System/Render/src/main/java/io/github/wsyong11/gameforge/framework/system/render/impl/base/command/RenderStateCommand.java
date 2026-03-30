package io.github.wsyong11.gameforge.framework.system.render.impl.base.command;

import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.command.RenderCommand;

public abstract class RenderStateCommand extends RenderCommand {
	public RenderStateCommand() {
		this.reset();
	}

	public abstract void reset();
}
