package io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw;

import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.Icon;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.WindowSurface;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.concurrent.ThreadMark;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.system.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class GLFWSurface implements WindowSurface {
	private static final Logger LOGGER = Log.getLogger();

	private long handler;
	private final ThreadMark owner;

	private final Vector2i size;

	private final ListenerList listenerList;

	private final List<Callback> callbacks;

	protected GLFWSurface(long handler, @NotNull ThreadMark owner) {
		Objects.requireNonNull(owner, "owner is null");

		owner.check();

		this.handler = handler;
		this.owner = owner;

		this.size = new Vector2i();

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
//		glfwSetWindowSizeCallback(this.handler, this.addCallback(GLFWWindowSizeCallback
//			.create((window, width, height) -> {
//				this.size.set(width, height);
//				this.fireWindowResized();
//			})));

//		glfwSetWindowPosCallback(this.handler, this.addCallback(GLFWWindowPosCallback
//			.create((window, x, y) -> {
//				this.position.set(x, y);
//				this.fireWindowMoved();
//			})));

//		glfwSetWindowCloseCallback(this.handler, this.addCallback(GLFWWindowCloseCallback
//			.create((window) -> this.fireWindowClose())));

//		glfwSetKeyCallback(this.handler, this.addCallback(GLFWKeyCallback
//			.create((window, keyCode, scanCode, action, mods) ->
//				this.listenerList.fire(
//					WindowInputListener.class,
//					l -> l.onKeyInput(
//						castKeyCode(keyCode),
//						castMods(mods),
//						castAction(action)),
//					ListenerExceptionCallback.log(LOGGER)))));
//
//		glfwSetMouseButtonCallback(this.handler, this.addCallback(GLFWMouseButtonCallback
//			.create((window, button, action, mods) ->
//				this.listenerList.fire(
//					WindowInputListener.class,
//					l -> l.onMouseInput(
//						castMouseButton(button),
//						castAction(action)),
//					ListenerExceptionCallback.log(LOGGER)))));
//
//		glfwSetCursorPosCallback(this.handler, this.addCallback(GLFWCursorPosCallback
//			.create((window, xPos, yPos) ->
//				this.listenerList.fire(
//					WindowInputListener.class,
//					l -> l.onMouseMove(xPos, yPos),
//					ListenerExceptionCallback.log(LOGGER)))));
//
//		glfwSetCursorEnterCallback(this.handler, this.addCallback(GLFWCursorEnterCallback
//			.create((window, entered) ->
//				this.listenerList.fire(
//					WindowInputListener.class,
//					l -> l.onMouseFocusChanged(entered),
//					ListenerExceptionCallback.log(LOGGER)))));
//
//		glfwSetScrollCallback(this.handler, this.addCallback(GLFWScrollCallback
//			.create((window, xOffset, yOffset) ->
//				this.listenerList.fire(
//					WindowInputListener.class,
//					l -> l.onMouseScroll(xOffset, yOffset),
//					ListenerExceptionCallback.log(LOGGER)))));
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

	}

	@Override
	public @NotNull Vector2ic getPosition() {
		return null;
	}

	@Override
	public void getPosition(@NotNull Vector2i dest) {

	}

	@Override
	public void setPosition(@NotNull Vector2ic pos) {

	}

	@Override
	public @NotNull String getTitle() {
		return "";
	}

	@Override
	public void setTitle(@NotNull String title) {

	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public void setVisible(boolean visible) {

	}

	@Override
	public boolean shouldClose() {
		return false;
	}

	@Override
	public void setShouldClose(boolean value) {

	}

	@Override
	public @Nullable Icon getIcon() {
		return null;
	}

	@Override
	public void setIcon(@Nullable Icon icon) {

	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public void present() {
		this.owner.checkAssert();
		glfwSwapBuffers(this.handler);
	}

	@Override
	public void pollEvents() {
		glfwPollEvents();
	}

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
