package io.github.wsyong11.gameforge.framework.system.graphic.vulkan;

import io.github.wsyong11.gameforge.util.Hex;
import io.github.wsyong11.gameforge.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

import java.util.Objects;

public class PhysicalDevice extends VulkanHandle<VkPhysicalDevice> {
	private final Lazy<Properties> properties;

	public PhysicalDevice(@NotNull VkPhysicalDevice instance, @NotNull Vulkan parent) {
		super(
			Objects.requireNonNull(instance, "instance is null"),
			parent
		);

		this.properties = Lazy.of(this::queryProperties);
	}

	@NotNull
	private Properties queryProperties() {
		VkPhysicalDevice instance = this.requireNative();
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VkPhysicalDeviceProperties deviceProperties = VkPhysicalDeviceProperties.malloc(stack);
			VK10.vkGetPhysicalDeviceProperties(instance, deviceProperties);

			return Properties.of(deviceProperties);
		}
	}

	@NotNull
	public Properties getProperties() {
		return this.properties.get();
	}

	@Nullable
	@Override
	public Vulkan getParent() {
		return (Vulkan) super.getParent();
	}

	@Override
	protected long getHandleImpl(@NotNull VkPhysicalDevice instance) {
		return instance.address();
	}

	public static class Properties {
		@NotNull
		public static Properties of(@NotNull VkPhysicalDeviceProperties properties) {
			Objects.requireNonNull(properties, "properties is null");

			return new Properties(
				properties.apiVersion(),
				properties.driverVersion(),
				properties.vendorID(),
				properties.deviceID(),
				properties.deviceType(),
				properties.deviceNameString(),
				Hex.toHex(properties.pipelineCacheUUID())
			);
		}

		private final int apiVersion;
		private final int driverVersion;
		private final int vendorId;
		private final int deviceId;
		private final int deviceType;
		private final String deviceName;
		private final String pipelinedCacheUUID;

		public Properties(
			int apiVersion,
			int driverVersion,
			int vendorId,
			int deviceId,
			int deviceType,
			@NotNull String deviceName,
			@NotNull String pipelinedCacheUUID
		) {
			Objects.requireNonNull(deviceName, "deviceName is null");
			Objects.requireNonNull(pipelinedCacheUUID, "pipelinedCacheUUID is null");

			this.apiVersion = apiVersion;
			this.driverVersion = driverVersion;
			this.vendorId = vendorId;
			this.deviceId = deviceId;
			this.deviceType = deviceType;
			this.deviceName = deviceName;
			this.pipelinedCacheUUID = pipelinedCacheUUID;
		}
	}
}
