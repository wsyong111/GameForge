package io.github.wsyong11.gameforge.framework.system.render.impl.base.command;

import io.github.wsyong11.gameforge.framework.annotation.Internal;
import org.jetbrains.annotations.Nullable;

public abstract class RenderCommand {
	@Nullable
	private volatile RenderCommand next;

	private int typeId;

	public RenderCommand() {
		this.next = null;
		this.typeId = -1;
	}

	@Internal
	public final void setNext(@Nullable RenderCommand next) {
		this.next = next;
	}

	@Nullable
	@Internal
	public final RenderCommand getNext() {
		return this.next;
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
