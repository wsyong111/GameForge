package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.memory;

import org.jetbrains.annotations.NotNull;

import static org.lwjgl.vulkan.VK10.VK_ERROR_OUT_OF_DEVICE_MEMORY;

public class VulkanDeviceOutOfMemoryException extends VulkanOutOfMemoryException {
	public VulkanDeviceOutOfMemoryException(@NotNull String message) {
		super(message, VK_ERROR_OUT_OF_DEVICE_MEMORY);
	}

	public VulkanDeviceOutOfMemoryException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause, VK_ERROR_OUT_OF_DEVICE_MEMORY);
	}
}
