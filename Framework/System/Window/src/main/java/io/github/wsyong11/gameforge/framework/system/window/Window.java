package io.github.wsyong11.gameforge.framework.system.window;

import io.github.wsyong11.gameforge.framework.system.window.listener.WindowInputListener;
import io.github.wsyong11.gameforge.framework.system.window.listener.WindowListener;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2ic;

public interface Window extends AutoCloseable {
	void setTitle(@NotNull String title);

	@NotNull
	String getTitle();

	void setVSyncType(@NotNull VSyncType type);

	@NotNull
	VSyncType getVSyncType();

	void setDisplayType(@NotNull WindowDisplayType type);

	@NotNull
	WindowDisplayType getDisplayType();

	void setWindowSize(@NotNull Vector2ic size);

	@NotNull
	Vector2ic getWindowSize();

	void setWindowPosition(@NotNull Vector2ic position);

	@NotNull
	Vector2ic getWindowPosition();

	void setVisible(boolean visible);

	boolean isVisible();

	// -------------------------------------------------------------------------------------------------------------- //

	void addWindowListener(@NotNull WindowListener listener);

	void removeWindowListener(@NotNull WindowListener listener);

	void addInputListener(@NotNull WindowInputListener listener);

	void removeInputListener(@NotNull WindowInputListener listener);

	@Override
	void close();
}
