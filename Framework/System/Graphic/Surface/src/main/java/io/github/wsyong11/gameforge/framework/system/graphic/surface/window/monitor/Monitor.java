package io.github.wsyong11.gameforge.framework.system.graphic.surface.window.monitor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.joml.primitives.Rectanglei;

import java.util.List;
import java.util.UUID;

public interface Monitor {
	@NotNull
	UUID getId();

	boolean isAvailable();

	boolean isPrimary();

	@NotNull
	String getName();

	@NotNull
	Vector2ic getPosition();

	@NotNull
	Rectanglei getWorkAreaRect(); // logical / current resolution

	@NotNull
	Vector2ic getPhysicalSize(); // mm

	@NotNull
	Vector2fc getContentScale();

	float getDPI();

	@NotNull
	MonitorVideoMode getCurrentVideoMode();

	@NotNull
	@UnmodifiableView
	List<MonitorVideoMode> getSupportedVideoModes();
}
