package io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw.opengl;

import io.github.wsyong11.gameforge.framework.system.graphic.core.info.RenderBackendInfo;
import io.github.wsyong11.gameforge.framework.system.graphic.core.info.RenderCapacityKey;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.semver4j.Semver;

import java.util.Objects;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLRenderBackendInfo implements RenderBackendInfo {
	private static final Logger LOGGER = Log.getLogger();

	private static final Lazy<RenderBackendInfo> INSTANCE = Lazy.of(GLRenderBackendInfo::detect);

	@Nullable
	private static GLRenderBackendInfo detect() {
		if (!glfwInit())
			return null;

		try {
			glfwDefaultWindowHints();
			glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
			glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);

			long window = glfwCreateWindow(1, 1, "", NULL, NULL);
			if (window == NULL)
				return null;

			glfwMakeContextCurrent(window);
			GL.createCapabilities();

			String vendor = glGetString(GL_VENDOR);
			Semver version = getOpenGLVersion();
			String renderer = glGetString(GL_RENDERER);

			glfwDestroyWindow(window);

			if (vendor == null || version == null || renderer == null)
				return null;

			return new GLRenderBackendInfo(vendor, version, renderer);
		} catch (Throwable e) {
			LOGGER.debug("Exception when detecting OpenGL info", e);
			return null;
		} finally {
			glfwTerminate();
		}
	}

	@Nullable
	private static Semver getOpenGLVersion() {
		try {
			int major = glGetInteger(GL30.GL_MAJOR_VERSION);
			int minor = glGetInteger(GL30.GL_MINOR_VERSION);

			if (major != NULL)
				return Semver.create(major, minor, 0);
		} catch (Throwable ignored) {
		}


		try {
			String version = GL11.glGetString(GL11.GL_VERSION);
			if (version == null)
				return null;

			String first = version.split(" ")[0];
			String[] parts = first.split("\\.");

			int major = Integer.parseInt(parts[0]);
			int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

			return Semver.create(major, minor, 0);

		} catch (Throwable ignored) {
		}

		return null;
	}

	@Nullable
	public static RenderBackendInfo getInstance() {
		return INSTANCE.get();
	}

	private final Semver version;
	private final String vendor;
	private final String device;

	protected GLRenderBackendInfo(@NotNull String vendor, @NotNull Semver version, @NotNull String device) {
		Objects.requireNonNull(vendor, "vendor is null");
		Objects.requireNonNull(version, "version is null");
		Objects.requireNonNull(device, "device is null");

		this.vendor = vendor;
		this.version = version;
		this.device = device;
	}

	@NotNull
	@Override
	public String getDriver() {
		return "OpenGL";
	}

	@NotNull
	@Override
	public Semver getVersion() {
		return this.version;
	}

	@NotNull
	@Override
	public String getVendor() {
		return this.vendor;
	}

	@NotNull
	@Override
	public String getDevice() {
		return this.device;
	}

	@NotNull
	@Override
	public Set<RenderCapacityKey<?>> getAllCapacity() {
		return Set.of();
	}

	@Nullable
	@Override
	public <T> T getCapacity(@NotNull RenderCapacityKey<T> key) {
		return null;
	}

	@Override
	public boolean hasCapacity(@NotNull RenderCapacityKey<?> key) {
		return false;
	}

	@Override
	public String toString() {
		return this.vendor + " OpenGL " + this.version + " " + this.device;
	}
}
