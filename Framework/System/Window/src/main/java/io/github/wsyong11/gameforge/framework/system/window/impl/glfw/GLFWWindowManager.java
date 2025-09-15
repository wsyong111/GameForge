package io.github.wsyong11.gameforge.framework.system.window.impl.glfw;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.window.*;
import io.github.wsyong11.gameforge.framework.system.window.ex.GraphicContextNotFound;
import io.github.wsyong11.gameforge.framework.system.window.ex.WindowCreatingException;
import io.github.wsyong11.gameforge.framework.system.window.ex.WindowException;
import io.github.wsyong11.gameforge.framework.system.window.impl.glfw.graphic.GLFWGraphicContext;
import io.github.wsyong11.gameforge.framework.system.window.impl.glfw.graphic.GraphicContextFactory;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2ic;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.util.Locale;
import java.util.Objects;

import static io.github.wsyong11.gameforge.framework.system.window.impl.glfw.GlfwUtils.freeCallback;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindowManager implements WindowManager {
	private static final Logger LOGGER = Log.getLogger();

	private final GLFWErrorCallback errorCallback;

	public GLFWWindowManager() {
		this.errorCallback = GLFWErrorCallback.create(this::onGlfwError);
	}

	private void onGlfwError(int error, long descriptionPtr) {
		LOGGER.warn("GLFW Error: 0x{}: {}",
			Integer.toHexString(error).toUpperCase(Locale.ROOT),
			MemoryUtil.memUTF8(descriptionPtr));
	}

	@Override
	public void init() throws WindowException {
		LOGGER.info("GLFW Version: {}", glfwGetVersionString());

		if (!glfwInit()) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				PointerBuffer pointerBuffer = stack.mallocPointer(1);
				if (glfwGetError(pointerBuffer) == GLFW_NO_ERROR)
					throw new WindowException("Cannot init glfw");

				String message = MemoryUtil.memUTF8(pointerBuffer.get());
				throw new WindowException("Cannot init glfw: " + message);
			}
		}

		glfwSetErrorCallback(this.errorCallback);
	}

	@NotNull
	@Override
	public Window createWindow(@NotNull WindowConfig config) throws WindowCreatingException {
		Objects.requireNonNull(config, "config is null");

		Vector2ic windowSize = config.getSize();
		Vector2ic position = config.getPosition();
		Identifier graphicApi = config.getApi();

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

		// TODO: 2025/9/15 Fix OpenGL Strong Coupling
		if (graphicApi.equals(WindowGraphicContext.OPENGL)) {
			WindowGraphicsConfig graphicsConfig = config.getGraphicsConfig();
			glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, graphicsConfig.getVersionMajor());
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, graphicsConfig.getVersionMinor());
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
			glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, graphicsConfig.getDebug() ? GLFW_TRUE : GLFW_FALSE);
		} else {
			glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
		}

		if (position != null) {
			glfwWindowHint(GLFW_POSITION_X, position.x());
			glfwWindowHint(GLFW_POSITION_Y, position.y());
		}

		LOGGER.debug("Creating the window using configuration {}", config);

		long window = glfwCreateWindow(
			windowSize.x(),
			windowSize.y(),
			config.getTitle(),
			NULL,
			NULL
		);
		if (window == NULL)
			throw new WindowCreatingException("Cannot create glfw window");

		GLFWGraphicContext graphicContext;
		try {
			graphicContext = GraphicContextFactory.get(graphicApi, window);
		} catch (GraphicContextNotFound e) {
			glfwDestroyWindow(window);
			throw e;
		}

		return new GLFWWindow(window, config, graphicContext);
	}

	@Override
	public void update() {
		glfwPollEvents();
	}

	@Override
	public long getNativeHandle(@NotNull Window window) {
		Objects.requireNonNull(window, "window is null");
		return GLFWWindow.cast(window).getHandler();
	}

	@Override
	public void close() {
		this.errorCallback.free();
		glfwTerminate();
	}
}
