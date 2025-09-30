package io.github.wsyong11.gameforge.framework.system.render.impl.base.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;
import java.util.Deque;
import java.util.LinkedList;

public class RenderCommandStack {
	private final Deque<RenderCommand> stack;

	public RenderCommandStack() {
		this.stack = new LinkedList<>();
	}

	public void push(byte opCode, @Nullable Buffer args) {
		this.stack.push(RenderCommand.acquire(opCode, args));
	}

	@NotNull
	public Deque<RenderCommand> toDeque() {
		return this.stack;
	}

	public void reset() {
		RenderCommand command;
		while ((command = this.stack.poll()) != null)
			RenderCommand.release(command);
	}
}
