package io.github.wsyong11.gameforge.framework.system.window.impl.glfw;

import io.github.wsyong11.gameforge.framework.system.window.Window;
import io.github.wsyong11.gameforge.framework.system.window.WindowConfig;
import io.github.wsyong11.gameforge.framework.system.window.WindowManager;
import io.github.wsyong11.gameforge.framework.system.window.ex.WindowCreatingException;
import io.github.wsyong11.gameforge.framework.system.window.ex.WindowException;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2ic;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindowManager implements WindowManager {
	@Override
	public void init() throws WindowException {
		if (glfwInit())
			return;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			PointerBuffer pointerBuffer = stack.mallocPointer(1);
			if (glfwGetError(pointerBuffer) == GLFW_NO_ERROR)
				throw new WindowException("Cannot init glfw");

			String message = MemoryUtil.memUTF8(pointerBuffer.get());
			throw new WindowException("Cannot init glfw: " + message);
		}
	}

	@NotNull
	@Override
	public Window createWindow(@NotNull WindowConfig config) throws WindowCreatingException {
		Objects.requireNonNull(config, "config is null");

		Vector2ic windowSize = config.getSize();
		long window = glfwCreateWindow(
			windowSize.x(),
			windowSize.y(),
			config.getTitle(),
			NULL,
			NULL
		);
		if (window == NULL)
			throw new WindowCreatingException("Cannot create glfw window");

		glfwMakeContextCurrent();

		return null;
	}

	@Override
	public long getNativeHandle(@NotNull Window window) {
		Objects.requireNonNull(window, "window is null");
		return GLFWWindow.cast(window).getHandler();
	}

	@Override
	public void close() {
		glfwTerminate();
	}
}
