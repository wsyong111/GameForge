package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.fatal;

import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.VulkanFatalException;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.vulkan.VK10.VK_ERROR_DEVICE_LOST;

public class VulkanDeviceLostException extends VulkanFatalException {
	public VulkanDeviceLostException(@NotNull String message) {
		super(message, VK_ERROR_DEVICE_LOST);
	}

	public VulkanDeviceLostException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause, VK_ERROR_DEVICE_LOST);
	}
}
