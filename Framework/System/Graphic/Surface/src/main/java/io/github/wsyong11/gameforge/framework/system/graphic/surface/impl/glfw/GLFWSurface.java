package io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw;

import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.WindowSurface;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.icon.Icon;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.listener.WindowInputListener;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.listener.WindowListener;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.concurrent.ThreadMark;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.*;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw.GLFWInputUtils.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class GLFWSurface implements WindowSurface {
	private static final Logger LOGGER = Log.getLogger();

	private long handler;
	private final ThreadMark owner;

	private final Vector2i size;
	private final Vector2i position;
	private String title;
	private boolean visible;

	private final ListenerList listenerList;

	private final List<Callback> callbacks;

	protected GLFWSurface(long handler, @NotNull ThreadMark owner) {
		Objects.requireNonNull(owner, "owner is null");

		owner.checkAssert();

		this.handler = handler;
		this.owner = owner;

		this.size = new Vector2i();
		this.position = new Vector2i();
		this.title = "";
		this.visible = false;

		this.listenerList = ListenerList.sync();

		this.callbacks = new ArrayList<>();
		this.initCallbacks();
	}

	// -------------------------------------------------------------------------------------------------------------- //
	// Native callback

	@Contract("_ -> param1")
	@NotNull
	private <T extends Callback> T addCallback(@NotNull T callback) {
		Objects.requireNonNull(callback, "callback is null");
		this.callbacks.add(callback);
		return callback;
	}

	private void initCallbacks() {
		glfwSetWindowSizeCallback(this.handler, this.addCallback(GLFWWindowSizeCallback
			.create((window, width, height) -> {
				this.size.set(width, height);
				this.fireWindowResized();
			})));

		glfwSetWindowPosCallback(this.handler, this.addCallback(GLFWWindowPosCallback
			.create((window, x, y) -> {
				this.position.set(x, y);
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

	private void assertWindow() {
		if (this.handler == NULL)
			throw new IllegalStateException("GLFW window has been destroyed or is invalid");
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@Override
	public Vector2ic getSize() {
		return new Vector2i(this.size);
	}

	@Override
	public void getSize(@NotNull Vector2i dest) {
		Objects.requireNonNull(dest, "dest is null");
		dest.set(this.size);
	}

	@Override
	public void setSize(@NotNull Vector2ic size) {
		Objects.requireNonNull(size, "size is null");
		this.owner.checkAssert();
		this.assertWindow();

		if (Objects.equals(this.size, size))
			return;

		this.size.set(size);
		glfwSetWindowSize(this.handler, this.size.x, this.size.y);
		this.fireWindowResized();
	}

	@NotNull
	@Override
	public Vector2ic getPosition() {
		return new Vector2i(this.position);
	}

	@Override
	public void getPosition(@NotNull Vector2i dest) {
		Objects.requireNonNull(dest, "dest is null");
		dest.set(this.position);
	}

	@Override
	public void setPosition(@Nullable Vector2ic pos) {
		this.owner.checkAssert();
		this.assertWindow();

		if (pos == null) {
			glfwSetWindowPos(this.handler, GLFW_ANY_POSITION, GLFW_ANY_POSITION);

			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer xBuffer = stack.mallocInt(1);
				IntBuffer yBuffer = stack.mallocInt(1);
				glfwGetWindowPos(this.handler, xBuffer, yBuffer);

				if (glfwGetError(null) == GLFW_FEATURE_UNAVAILABLE)
					this.position.set(0, 0);
				else
					this.position.set(
						xBuffer.get(),
						yBuffer.get()
					);
			}
			return;
		}

		if (Objects.equals(this.position, pos))
			return;

		this.position.set(pos);
		glfwSetWindowPos(this.handler, this.position.x, this.position.y);

		if (glfwGetError(null) == GLFW_FEATURE_UNAVAILABLE)
			LOGGER.warn("Cannot set window position to {} from {}", this.position, this);

		this.fireWindowMoved();
	}

	@NotNull
	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public void setTitle(@NotNull String title) {
		Objects.requireNonNull(title, "title is null");
		this.owner.checkAssert();
		this.assertWindow();

		if (Objects.equals(this.title, title))
			return;

		this.title = title;
		glfwSetWindowTitle(this.handler, title);
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.owner.checkAssert();
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
	public boolean shouldClose() {
		this.owner.checkAssert();
		this.assertWindow();
		return glfwWindowShouldClose(this.handler);
	}

	@Override
	public void setShouldClose(boolean value) {
		this.owner.checkAssert();
		this.assertWindow();
		glfwSetWindowShouldClose(this.handler, value);
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public void setIcon(@Nullable Icon icon) {
		// TODO: 2026/5/19
		throw new UnsupportedOperationException("setIcon");
	}

	@Override
	public boolean isValid() {
		return this.handler != NULL;
	}

	@Override
	public void present() {
		this.owner.checkAssert();
		this.assertWindow();
		glfwSwapBuffers(this.handler);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private void fireWindowResized() {
		this.listenerList.fire(
			WindowListener.class,
			l -> l.onResize(new Vector2i(this.size)),
			ListenerExceptionCallback.log(LOGGER));
	}

	private void fireWindowMoved() {
		this.listenerList.fire(
			WindowListener.class,
			l -> l.onMove(new Vector2i(this.position)),
			ListenerExceptionCallback.log(LOGGER));
	}

	private void fireWindowClose() {
		this.listenerList.fire(
			WindowListener.class,
			WindowListener::onClose,
			ListenerExceptionCallback.log(LOGGER));
	}


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

	@Override
	public void close() {
		if (this.handler == NULL)
			return;

		long handler = this.handler;
		this.handler = NULL;

		this.listenerList.clear();

		for (Callback callback : this.callbacks)
			callback.free();

		this.callbacks.clear();

//		this.icon = null;
		glfwSetWindowIcon(handler, null);

		glfwDestroyWindow(handler);
	}

	@Override
	public String toString() {
		if (this.handler == NULL)
			return "GLFWSurface[NULL]";

		return "GLFWSurface[0x%08X]".formatted(this.handler);
	}
}
