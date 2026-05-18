package io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw.opengl;

import io.github.wsyong11.gameforge.framework.system.graphic.core.info.RenderBackedInfo;
import io.github.wsyong11.gameforge.framework.system.graphic.core.info.RenderDeviceInfo;
import io.github.wsyong11.gameforge.framework.system.graphic.core.info.RenderFutureKey;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.semver4j.Semver;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLRenderBackedInfo implements RenderBackedInfo {
	private static final Logger LOGGER = Log.getLogger();

	private static final Lazy<RenderBackedInfo> INSTANCE = Lazy.of(GLRenderBackedInfo::detect);

	@Nullable
	private static GLRenderBackedInfo detect() {
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

			return new GLRenderBackedInfo(vendor, version, renderer);
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
	public static RenderBackedInfo getInstance() {
		return INSTANCE.get();
	}

	private final Semver version;
	private final String vendor;

	protected GLRenderBackedInfo(@NotNull String vendor, @NotNull Semver version, @NotNull String device) {
		Objects.requireNonNull(vendor, "vendor is null");
		Objects.requireNonNull(version, "version is null");
		Objects.requireNonNull(device, "device is null");

		this.vendor = vendor;
		this.version = version;
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

	@Override
	public String toString() {
		return this.vendor + " OpenGL " + this.version;
	}

	@NotNull
	@Override
	public List<RenderDeviceInfo> getDevices() {
		return List.of();
	}

	private static class GLDeviceInfo implements RenderDeviceInfo {
		private final String deviceName;
		private final String vendorName;
		private final UUID id;
		private final long vram;

		private GLDeviceInfo(@NotNull String deviceName, @NotNull String vendorName, @NotNull UUID id, long vram) {
			Objects.requireNonNull(deviceName, "deviceName is null");
			this.deviceName = deviceName;
			this.vendorName = vendorName;
			this.id = id;
			this.vram = vram;
		}

		@NotNull
		@Override
		public String getDeviceName() {
			return this.deviceName;
		}

		@NotNull
		@Override
		public String getVendorName() {
			return this.vendorName;
		}

		@NotNull
		@Override
		public UUID getId() {
			return this.id;
		}

		@Override
		public long getVram() {
			return this.vram;
		}

		@Nullable
		@Override
		public <T> T getFuture(@NotNull RenderFutureKey<T> key) {
			return null;
		}

		@Override
		public boolean hasFuture(@NotNull RenderFutureKey<?> key) {
			return false;
		}
	}
}
