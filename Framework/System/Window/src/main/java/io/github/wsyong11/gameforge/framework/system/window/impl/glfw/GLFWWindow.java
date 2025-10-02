package io.github.wsyong11.gameforge.framework.system.window.impl.glfw;

import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.window.Window;
import io.github.wsyong11.gameforge.framework.system.window.WindowConfig;
import io.github.wsyong11.gameforge.framework.system.window.WindowDisplayType;
import io.github.wsyong11.gameforge.framework.system.window.WindowGraphicContext;
import io.github.wsyong11.gameforge.framework.system.window.icon.Icon;
import io.github.wsyong11.gameforge.framework.system.window.impl.glfw.graphic.GLFWGraphicContext;
import io.github.wsyong11.gameforge.framework.system.window.listener.WindowInputListener;
import io.github.wsyong11.gameforge.framework.system.window.listener.WindowListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.wsyong11.gameforge.framework.system.window.impl.glfw.GLFWInputUtils.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindow implements Window {
	private static final Logger LOGGER = Log.getLogger();

	@Contract("null -> null; !null -> !null")
	@Nullable
	public static GLFWWindow cast(@Nullable Window window) {
		if (window == null)
			return null;

		if (window instanceof GLFWWindow glfwWindow)
			return glfwWindow;

		throw new ClassCastException("Cannot cast the window to glfw implementation");
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private long handler;
	private GLFWGraphicContext graphicContext;

	private final List<Callback> callbacks;

	private final ListenerList listenerList;

	private String title;
	private WindowDisplayType displayType;
	private final Vector2i windowSize;
	private final Vector2i windowPosition;
	private boolean visible;
	private Icon icon;

	public GLFWWindow(long handler, @NotNull WindowConfig config, @NotNull GLFWGraphicContext graphicContext) {
		Objects.requireNonNull(config, "config is null");
		Objects.requireNonNull(graphicContext, "graphicContext is null");

		if (handler == NULL)
			throw new IllegalArgumentException("Null window handler");

		this.handler = handler;
		this.graphicContext = graphicContext;

		this.callbacks = new ArrayList<>();

		this.listenerList = ListenerList.sync();

		this.title = "";
		this.displayType = WindowDisplayType.WINDOW;
		this.windowSize = new Vector2i();
		this.windowPosition = new Vector2i();
		this.visible = false;
		this.icon = null;

		this.initCallback();

		this.setTitle(config.getTitle());
		this.setDisplayType(config.getDisplayType());
		this.setWindowSize(config.getSize());
		this.setWindowPosition(config.getPosition());
	}

	@Contract("_ -> param1")
	@NotNull
	private <T extends Callback> T addCallback(@NotNull T callback) {
		Objects.requireNonNull(callback, "callback is null");
		this.callbacks.add(callback);
		return callback;
	}

	private void initCallback() {
		glfwSetWindowSizeCallback(this.handler, this.addCallback(GLFWWindowSizeCallback
			.create((window, width, height) -> {
				this.windowSize.set(width, height);
				this.fireWindowResized();
			})));

		glfwSetWindowPosCallback(this.handler, this.addCallback(GLFWWindowPosCallback
			.create((window, x, y) -> {
				this.windowPosition.set(x, y);
				this.fireWindowMoved();
			})));

		glfwSetWindowCloseCallback(this.handler, this.addCallback(GLFWWindowCloseCallback
			.create((window) -> this.fireWindowClose())));

		glfwSetKeyCallback(this.handler, this.addCallback(GLFWKeyCallback
			.create((window, keyCode, scanCode, action, mods) ->
				this.listenerList.fire(
					WindowInputListener.class,
					l -> l.onKeyInput(
						castKeyCode(keyCode),
						castMods(mods),
						castAction(action)),
					ListenerExceptionCallback.log(LOGGER)))));

		glfwSetMouseButtonCallback(this.handler, this.addCallback(GLFWMouseButtonCallback
			.create((window, button, action, mods) ->
				this.listenerList.fire(
					WindowInputListener.class,
					l -> l.onMouseInput(
						castMouseButton(button),
						castAction(action)),
					ListenerExceptionCallback.log(LOGGER)))));

		glfwSetCursorPosCallback(this.handler, this.addCallback(GLFWCursorPosCallback
			.create((window, xPos, yPos) ->
				this.listenerList.fire(
					WindowInputListener.class,
					l -> l.onMouseMove(xPos, yPos),
					ListenerExceptionCallback.log(LOGGER)))));

		glfwSetCursorEnterCallback(this.handler, this.addCallback(GLFWCursorEnterCallback
			.create((window, entered) ->
				this.listenerList.fire(
					WindowInputListener.class,
					l -> l.onMouseFocusChanged(entered),
					ListenerExceptionCallback.log(LOGGER)))));

		glfwSetScrollCallback(this.handler, this.addCallback(GLFWScrollCallback
			.create((window, xOffset, yOffset) ->
				this.listenerList.fire(
					WindowInputListener.class,
					l -> l.onMouseScroll(xOffset, yOffset),
					ListenerExceptionCallback.log(LOGGER)))));
	}

	public long getHandler() {
		return this.handler;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private void fireWindowResized() {
		this.listenerList.fire(
			WindowListener.class,
			l -> l.onResize(this.windowSize),
			ListenerExceptionCallback.log(LOGGER));
	}

	private void fireWindowMoved() {
		this.listenerList.fire(
			WindowListener.class,
			l -> l.onMove(this.windowSize),
			ListenerExceptionCallback.log(LOGGER));
	}

	private void fireWindowClose() {
		this.listenerList.fire(
			WindowListener.class,
			WindowListener::onClose,
			ListenerExceptionCallback.log(LOGGER));
	}

	private long getWindowMonitor() {
		PointerBuffer monitorPointers = glfwGetMonitors();
		if (monitorPointers == null)
			return NULL;

		int monitorSize = monitorPointers.limit();
		if (monitorSize <= 1)
			return monitorPointers.get();

		for (int i = 0; i < monitorSize; i++) {
			long monitor = monitorPointers.get(i);

			int[] monX = new int[1];
			int[] monY = new int[1];
			glfwGetMonitorPos(monitor, monX, monY);

			GLFWVidMode mode = glfwGetVideoMode(monitor);
			if (mode == null)
				continue;

			int monX0 = monX[0];
			int monY0 = monY[0];
			int monX1 = monX0 + mode.width();
			int monY1 = monY0 + mode.height();

			if (this.windowPosition.x >= monX0 && this.windowPosition.x < monX1 &&
				this.windowPosition.y >= monY0 && this.windowPosition.y < monY1)
				return monitor;
		}

		return NULL;
	}

	private void assertWindow() {
		if (this.handler == NULL)
			throw new IllegalStateException("GLFW window has been destroyed or is invalid");
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public void setTitle(@NotNull String title) {
		Objects.requireNonNull(title, "title is null");

		this.assertWindow();

		if (Objects.equals(this.title, title))
			return;

		this.title = title;
		glfwSetWindowTitle(this.handler, title);
	}

	@NotNull
	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public void setDisplayType(@NotNull WindowDisplayType type) {
		Objects.requireNonNull(type, "type is null");

		this.assertWindow();

		if (this.displayType == type)
			return;

		this.displayType = type;

		// Get window vid mod and monitor to recreate the window
		long monitor = this.getWindowMonitor();
		GLFWVidMode vid = glfwGetVideoMode(monitor);
		if (monitor == NULL || vid == null) {
			LOGGER.error("Cannot update display type to {}, cannot get current monitor vid mode", this.displayType);
			return;
		}

		switch (this.displayType) {
			case WINDOW -> {
				glfwSetWindowAttrib(this.handler, GLFW_DECORATED, GLFW_TRUE);
				glfwSetWindowMonitor(
					this.handler,
					NULL,
					this.windowPosition.x,
					this.windowPosition.y,
					this.windowSize.x,
					this.windowSize.y,
					GLFW_DONT_CARE);
			}
			case FULL_SCREEN -> {
				glfwSetWindowAttrib(this.handler, GLFW_DECORATED, GLFW_FALSE);
				glfwSetWindowMonitor(this.handler, NULL, 0, 0, vid.width(), vid.height(), GLFW_DONT_CARE);
			}
			case EXCLUSIVE ->
				glfwSetWindowMonitor(this.handler, monitor, 0, 0, vid.width(), vid.height(), vid.refreshRate());
		}
	}

	@NotNull
	@Override
	public WindowDisplayType getDisplayType() {
		return this.displayType;
	}

	@Override
	public void setWindowSize(@NotNull Vector2ic size) {
		Objects.requireNonNull(size, "size is null");

		this.assertWindow();

		if (Objects.equals(this.windowSize, size))
			return;

		this.windowSize.set(size);
		glfwSetWindowSize(this.handler, this.windowSize.x, this.windowSize.y);
		this.fireWindowResized();
	}

	@NotNull
	@Override
	public Vector2ic getWindowSize() {
		return this.windowSize;
	}

	@Override
	public void setWindowPosition(@Nullable Vector2ic position) {
		this.assertWindow();

		if (position == null) {
			glfwSetWindowPos(this.handler, GLFW_ANY_POSITION, GLFW_ANY_POSITION);

			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer xBuffer = stack.mallocInt(1);
				IntBuffer yBuffer = stack.mallocInt(1);
				glfwGetWindowPos(this.handler, xBuffer, yBuffer);

				if (glfwGetError(null) == GLFW_FEATURE_UNAVAILABLE)
					this.windowPosition.set(0, 0);

				this.windowPosition.set(
					xBuffer.get(),
					yBuffer.get()
				);
			}
			return;
		}

		if (Objects.equals(this.windowPosition, position))
			return;

		this.windowPosition.set(position);
		glfwSetWindowPos(this.handler, this.windowPosition.x, this.windowPosition.y);
		if (glfwGetError(null) == GLFW_FEATURE_UNAVAILABLE)
			LOGGER.warn("Cannot set window position: {}", this);

		this.fireWindowMoved();
	}

	@NotNull
	@Override
	public Vector2ic getWindowPosition() {
		return this.windowPosition;
	}

	@Override
	public void setVisible(boolean visible) {
		this.assertWindow();

		if (this.visible == visible)
			return;

		this.visible = visible;
		if (visible)
			glfwShowWindow(this.handler);
		else
			glfwHideWindow(this.handler);
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public boolean shouldClose() {
		this.assertWindow();
		return glfwWindowShouldClose(this.handler);
	}

	@Override
	public void setShouldClose(boolean value) {
		this.assertWindow();
		glfwSetWindowShouldClose(this.handler, value);
	}

	@Override
	public void setIcon(@Nullable Icon icon) {
		this.assertWindow();

		if (Objects.equals(this.icon, icon))
			return;

		this.icon = icon;

		if (icon == null) {
			glfwSetWindowIcon(this.handler, null);
			return;
		}

		if (icon.isClosed())
			throw new IllegalArgumentException("Icon is closed");

		try (MemoryStack stack = MemoryStack.stackPush()) {
			List<Vector2ic> sizes = icon.getSizes();
			int iconCount = sizes.size();

			GLFWImage.Buffer icons = GLFWImage.malloc(iconCount, stack);

			for (int i = 0; i < iconCount; i++) {
				Vector2ic size = sizes.get(i);

				icons.position(i)
				     .width(size.x())
				     .height(size.y())
				     .pixels(icon.getImage(i));
			}

			icons.position(0);
			GLFW.glfwSetWindowIcon(this.handler, icons);
		}
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return this.icon;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public void addWindowListener(@NotNull WindowListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.add(WindowListener.class, listener);
	}

	@Override
	public void removeWindowListener(@NotNull WindowListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.remove(WindowListener.class, listener);
	}

	@Override
	public void addInputListener(@NotNull WindowInputListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.add(WindowInputListener.class, listener);
	}

	@Override
	public void removeInputListener(@NotNull WindowInputListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.remove(WindowInputListener.class, listener);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@Override
	public WindowGraphicContext getGraphicContext() {
		this.assertWindow();
		return this.graphicContext;
	}

	@Override
	public void close() {
		if (this.handler == NULL)
			return;

		long handler = this.handler;
		this.handler = NULL;

		this.listenerList.clear();

		this.graphicContext.close();
		this.graphicContext = null;

		for (Callback callback : this.callbacks)
			callback.free();

		this.callbacks.clear();

		this.icon = null;
		glfwSetWindowIcon(handler, null);
		
		glfwDestroyWindow(handler);
	}

	@Override
	public String toString() {
		if (this.handler == NULL)
			return "GLFWWindow[NULL]";

		return "GLFWWindow[0x%08X]".formatted(this.handler);
	}
}
