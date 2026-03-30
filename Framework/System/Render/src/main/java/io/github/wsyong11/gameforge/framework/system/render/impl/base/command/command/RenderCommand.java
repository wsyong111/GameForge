package io.github.wsyong11.gameforge.framework.system.render.impl.base.command.command;

import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.RenderCommands;

public abstract class RenderCommand {
	private volatile int typeId;

	public RenderCommand() {
		this.typeId = -1;
	}

	public final int getTypeId() {
		if (this.typeId == -1) {
			this.typeId = RenderCommands.getId(this.getClass());
			return this.typeId;
		}

		return this.typeId;
	}

	public void reset() {
	}
}
