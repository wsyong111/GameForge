package io.github.wsyong11.gameforge.framework.system.graphic.surface.window;

import io.github.wsyong11.gameforge.framework.system.graphic.surface.Surface;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.icon.Icon;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.listener.WindowInputListener;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.listener.WindowListener;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.monitor.Monitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public interface WindowSurface extends Surface {
	@Override
	@NotNull
	Vector2ic getSize();

	@Override
	void getSize(@NotNull Vector2i dest);

	void setSize(@NotNull Vector2ic size);


	@NotNull
	Vector2ic getPosition();

	void getPosition(@NotNull Vector2i dest);

	// Null = System position
	void setPosition(@Nullable Vector2ic pos);


	@NotNull
	String getTitle();

	void setTitle(@NotNull String title);


	boolean isVisible();

	void setVisible(boolean visible);


	boolean shouldClose();

	void setShouldClose(boolean value);

	@Nullable
	Icon getIcon();

	void setIcon(@Nullable Icon icon);


	@NotNull
	WindowMode getWindowMode();

	void setWindowMode(@NotNull WindowMode mode);


	@NotNull
	Monitor getCurrentMonitor();

	@Nullable
	Monitor getFullscreenMonitor();

	// null = current monitor
	void setFullscreenMonitor(@Nullable Monitor monitor);


	void addWindowListener(@NotNull WindowListener listener);

	void removeWindowListener(@NotNull WindowListener listener);

	void addInputListener(@NotNull WindowInputListener listener);

	void removeInputListener(@NotNull WindowInputListener listener);
}
