package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.memory;

import org.jetbrains.annotations.NotNull;

import static org.lwjgl.vulkan.VK10.VK_ERROR_OUT_OF_HOST_MEMORY;

public class VulkanHostOutOfMemoryException extends VulkanOutOfMemoryException {
	public VulkanHostOutOfMemoryException(@NotNull String message) {
		super(message, VK_ERROR_OUT_OF_HOST_MEMORY);
	}

	public VulkanHostOutOfMemoryException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause, VK_ERROR_OUT_OF_HOST_MEMORY);
	}
}
