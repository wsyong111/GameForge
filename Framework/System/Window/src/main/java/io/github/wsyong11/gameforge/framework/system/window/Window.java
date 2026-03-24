package io.github.wsyong11.gameforge.framework.system.window;

import io.github.wsyong11.gameforge.framework.system.window.icon.Icon;
import io.github.wsyong11.gameforge.framework.system.window.listener.WindowInputListener;
import io.github.wsyong11.gameforge.framework.system.window.listener.WindowListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2ic;

/**
 * 窗口对象，包含各种属性和函数可控制窗口状态，窗口应由{@link WindowManager 窗口管理器}管理，在管理器释放后窗口将失效
 */
// TODO: 2026/3/24 现代化API，vector复用
public interface Window extends AutoCloseable {
	void setTitle(@NotNull String title);

	@NotNull
	String getTitle();

	void setDisplayType(@NotNull WindowDisplayType type);

	@NotNull
	WindowDisplayType getDisplayType();

	void setWindowSize(@NotNull Vector2ic size);

	@NotNull
	Vector2ic getWindowSize();

	/**
	 * 设定窗口位置
	 *
	 * @param position 窗口的位置，如果为空则让系统决定位置
	 */
	void setWindowPosition(@Nullable Vector2ic position);

	@NotNull
	Vector2ic getWindowPosition();

	void setVisible(boolean visible);

	boolean isVisible();

	boolean shouldClose();

	void setShouldClose(boolean value);

	void setIcon(@Nullable Icon icon);

	@Nullable
	Icon getIcon();

	// -------------------------------------------------------------------------------------------------------------- //

	void addWindowListener(@NotNull WindowListener listener);

	void removeWindowListener(@NotNull WindowListener listener);

	void addInputListener(@NotNull WindowInputListener listener);

	void removeInputListener(@NotNull WindowInputListener listener);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	WindowGraphicContext getGraphicContext();

	@Override
	void close();
}
