package io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw;

import io.github.wsyong11.gameforge.framework.annotation.UnsafeAPI;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.monitor.Monitor;
import io.github.wsyong11.gameforge.framework.system.graphic.surface.window.monitor.MonitorVideoMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWMonitor implements Monitor, AutoCloseable {
	@NotNull
	public static GLFWMonitor of(long monitor) {
		GLFWMonitor instance = new GLFWMonitor(monitor);
		instance.fullUpdate();
		return instance;
	}

	@NotNull
	private static MonitorVideoMode convertWithGlfw(@NotNull GLFWVidMode videoMode) {
		Objects.requireNonNull(videoMode, "videoMode is null");

		return new MonitorVideoMode(
			videoMode.width(),
			videoMode.height(),
			videoMode.refreshRate()
		);
	}

	private long handler;

	private boolean primary;
	private final Vector2f scale;
	private final Vector2i position;

	private UUID id;
	@Nullable
	private String name;

	private final Vector2i physicalSize;
	private MonitorVideoMode currentVideoMode;
	private List<MonitorVideoMode> supportedVideoModes;

	protected GLFWMonitor(long handler) {
		this.handler = handler;

		this.primary = false;
		this.scale = new Vector2f();
		this.position = new Vector2i();
		this.size = new Vector2i();

		this.id = UUID.randomUUID();
		this.name = null;

		this.physicalSize = new Vector2i();
		this.currentVideoMode = MonitorVideoMode.UNKNOWN;
		this.supportedVideoModes = List.of();
	}

	@UnsafeAPI
	public long getHandler() {
		return this.handler;
	}

	private void assertAvailable() {
		if (this.handler == NULL)
			throw new IllegalStateException("This monitor instance is not available");
	}

	public void update() {
		this.assertAvailable();

		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer scaleXBuf = stack.mallocFloat(1);
			FloatBuffer scaleYBuf = stack.mallocFloat(1);
			glfwGetMonitorContentScale(this.handler, scaleXBuf, scaleYBuf);

			this.scale.set(
				scaleXBuf.get(),
				scaleYBuf.get()
			);

			IntBuffer posXBuf = stack.mallocInt(1);
			IntBuffer posYBuf = stack.mallocInt(1);
			glfwGetMonitorPos(this.handler, posXBuf, posYBuf);

			this.position.set(
				posXBuf.get(),
				posYBuf.get()
			);

			glfwGetMonitorWorkarea();
		}

		this.name = glfwGetMonitorName(this.handler);
		this.primary = this.handler == glfwGetPrimaryMonitor();
	}

	public void fullUpdate() {
		this.assertAvailable();

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer physicalSizeXBuf = stack.mallocInt(1);
			IntBuffer physicalSizeYBuf = stack.mallocInt(1);
			glfwGetMonitorPhysicalSize(this.handler, physicalSizeXBuf, physicalSizeYBuf);

			this.physicalSize.set(
				physicalSizeXBuf.get(),
				physicalSizeYBuf.get()
			);
		}

		GLFWVidMode currentVideoMode = glfwGetVideoMode(this.handler);
		this.currentVideoMode = currentVideoMode == null
			? MonitorVideoMode.UNKNOWN
			: convertWithGlfw(currentVideoMode);

		GLFWVidMode.Buffer videoModes = glfwGetVideoModes(this.handler);
		this.supportedVideoModes = IntStream
			.range(0, videoModes.limit())
			.mapToObj(videoModes::get)
			.map(GLFWMonitor::convertWithGlfw)
			.sorted()
			.toList();

		this.update();

		String idText = (this.name == null ? "" : this.name)
			+ "@"
			+ this.physicalSize.x + "x" + this.physicalSize.y
			+ " "
			+ this.currentVideoMode.getRefreshRate()
			+ "fps";

		this.id = UUID.nameUUIDFromBytes(idText.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public boolean isAvailable() {
		return this.handler != NULL;
	}

	@NotNull
	@Override
	public UUID getId() {
		return this.id;
	}

	@Override
	public boolean isPrimary() {
		return this.primary;
	}

	@NotNull
	@Override
	public String getName() {
		return this.name == null ? "Monitor-" + this.id : this.name;
	}

	@NotNull
	@Override
	public Vector2ic getPosition() {
		return new Vector2i(this.position);
	}

	@NotNull
	@Override
	public Vector2ic getSize() {
		if (!this.currentVideoMode.isAvailable())
			return new Vector2i(0, 0);

		return new Vector2i(this.currentVideoMode.getWidth(), this.currentVideoMode.getHeight());
	}

	@NotNull
	@Override
	public Vector2ic getPhysicalSize() {
		return new Vector2i(this.physicalSize);
	}

	@NotNull
	@Override
	public Vector2fc getContentScale() {
		return new Vector2f(this.scale);
	}

	@Override
	public float getDPI() {
		return 0;
	}

	@NotNull
	@Override
	public MonitorVideoMode getCurrentVideoMode() {
		return this.currentVideoMode;
	}

	@NotNull
	@UnmodifiableView
	@Override
	public List<MonitorVideoMode> getSupportedVideoModes() {
		return this.supportedVideoModes;
	}

	@Override
	public void close() {
		this.handler = NULL;

		this.primary = false;
		this.physicalSize.set(0);
		this.scale.set(0);
		this.position.set(0);
		this.currentVideoMode = MonitorVideoMode.UNKNOWN;
		this.supportedVideoModes = List.of();
	}
}
