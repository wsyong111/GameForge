package io.github.wsyong11.gameforge.framework.system.render.impl.base.command;

import io.github.wsyong11.gameforge.util.pool.ObjectPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;
import java.util.Objects;

public final class RenderCommand {
	private static final ObjectPool<RenderCommand> POOL = ObjectPool.create(8192, RenderCommand::new, RenderCommand::reset);

	@NotNull
	public static RenderCommand acquire(byte opCode, @Nullable Buffer args) {
		RenderCommand command = POOL.acquire();
		command.set(opCode, args);
		return command;
	}

	public static void release(@Nullable RenderCommand command) {
		POOL.release(command);
	}

	private byte opCode;
	private Buffer args;

	private RenderCommand() {   
		this.reset();
	}

	private void reset() {
		this.opCode = 0;
		this.args = null;
	}

	private void set(byte opCode, @Nullable Buffer args) {
		this.opCode = opCode;
		this.args = args;
	}

	public byte getOpCode() {
		return this.opCode;
	}

	@Nullable
	public Buffer getArgs() {
		return this.args;
	}
}
