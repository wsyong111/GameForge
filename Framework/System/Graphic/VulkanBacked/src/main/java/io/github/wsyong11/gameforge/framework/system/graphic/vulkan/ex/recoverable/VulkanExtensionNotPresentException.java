package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.recoverable;

import org.jetbrains.annotations.NotNull;

import static org.lwjgl.vulkan.VK10.VK_ERROR_EXTENSION_NOT_PRESENT;

public class VulkanExtensionNotPresentException extends VulkanRecoverableException {
	public VulkanExtensionNotPresentException(@NotNull String message) {
		super(message, VK_ERROR_EXTENSION_NOT_PRESENT);
	}

	public VulkanExtensionNotPresentException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause, VK_ERROR_EXTENSION_NOT_PRESENT);
	}
}
