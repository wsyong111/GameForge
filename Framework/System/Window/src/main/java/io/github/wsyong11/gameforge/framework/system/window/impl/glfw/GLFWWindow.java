package io.github.wsyong11.gameforge.framework.system.window.impl.glfw;

import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.system.window.VSyncType;
import io.github.wsyong11.gameforge.framework.system.window.Window;
import io.github.wsyong11.gameforge.framework.system.window.WindowConfig;
import io.github.wsyong11.gameforge.framework.system.window.WindowDisplayType;
import io.github.wsyong11.gameforge.framework.system.window.listener.WindowInputListener;
import io.github.wsyong11.gameforge.framework.system.window.listener.WindowListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindow implements Window {
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

	private final ListenerList listenerList;

	private String title;
	private VSyncType vSyncType;
	private WindowDisplayType displayType;
	private final Vector2i windowSize;
	private final Vector2i windowPosition;
	private boolean visible;

	public GLFWWindow(long handler, @NotNull WindowConfig config) {
		if (handler == NULL)
			throw new IllegalArgumentException("Null window handler");

		this.handler = handler;

		this.setTitle(config.getTitle());
		this.setTitle(config.getTitle());
	}

	long getHandler() {
		return this.handler;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public void setTitle(@NotNull String title) {
		Objects.requireNonNull(title, "title is null");

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
	public void setVSyncType(@NotNull VSyncType type) {
		Objects.requireNonNull(type, "type is null");

	}

	@Override
	public @NotNull VSyncType getVSyncType() {
		return null;
	}

	@Override
	public void setDisplayType(@NotNull WindowDisplayType type) {

	}

	@Override
	public @NotNull WindowDisplayType getDisplayType() {
		return null;
	}

	@Override
	public void setWindowSize(@NotNull Vector2ic size) {

	}

	@Override
	public @NotNull Vector2ic getWindowSize() {
		return null;
	}

	@Override
	public void setWindowPosition(@NotNull Vector2ic position) {

	}

	@Override
	public @NotNull Vector2ic getWindowPosition() {
		return null;
	}

	@Override
	public void setVisible(boolean visible) {

	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public void addWindowListener(@NotNull WindowListener listener) {

	}

	@Override
	public void removeWindowListener(@NotNull WindowListener listener) {

	}

	@Override
	public void addInputListener(@NotNull WindowInputListener listener) {

	}

	@Override
	public void removeInputListener(@NotNull WindowInputListener listener) {

	}

	@Override
	public void close() {
		if (this.handler == NULL)
			return;

	}
}
