package io.github.wsyong11.gameforge.framework.system.window.impl.glfw.graphic;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.window.VSyncType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLGraphicContext extends GLFWGraphicContext {
	private VSyncType vSyncType;

	public OpenGLGraphicContext(long handler) {
		super(handler);
		this.vSyncType = VSyncType.DISABLE;
	}

	@NotNull
	@Override
	public Identifier getType() {
		return OPENGL;
	}

	@Override
	public void setVSyncType(@NotNull VSyncType type) {
		Objects.requireNonNull(type, "type is null");

		if (this.vSyncType == type)
			return;

		this.vSyncType = type;

		glfwSwapInterval(switch (type) {
			case DISABLE -> 0;
			case V_SYNC -> 1;
			case ADAPTIVE_VSYNC -> -1;
		});
	}

	@NotNull
	@Override
	public VSyncType getVSyncType() {
		return this.vSyncType;
	}

	@Override
	public void bind() {
		long handler = this.getHandler();
		if (handler == NULL)
			return;

		glfwMakeContextCurrent(handler);
	}

	@Override
	public void unbind() {
		glfwMakeContextCurrent(NULL);
	}

	@Override
	public void swap() {
		long handler = this.getHandler();
		if (handler == NULL)
			return;

		glfwSwapBuffers(handler);
	}
}
