package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.recoverable;

import org.jetbrains.annotations.NotNull;

public class VulkanSwapchainException extends VulkanRecoverableException {
	public VulkanSwapchainException(@NotNull String message, int errorCode) {
		super(message, errorCode);
	}

	public VulkanSwapchainException(@NotNull String message, @NotNull Throwable cause, int errorCode) {
		super(message, cause, errorCode);
	}
}
