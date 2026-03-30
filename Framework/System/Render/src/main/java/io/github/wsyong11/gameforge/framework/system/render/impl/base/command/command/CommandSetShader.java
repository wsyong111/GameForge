package io.github.wsyong11.gameforge.framework.system.render.impl.base.command.command;

import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.RenderStateCommand;

public class CommandSetShader extends RenderStateCommand {
	private volatile int shaderId;

	public int getShaderId() {
		return this.shaderId;
	}

	public void set(int shaderId) {
		this.shaderId = shaderId;
	}

	@Override
	public void reset() {
		this.shaderId = -1;
	}
}
