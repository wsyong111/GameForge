package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.recoverable;

import org.jetbrains.annotations.NotNull;

import static org.lwjgl.vulkan.VK10.VK_ERROR_LAYER_NOT_PRESENT;

public class VulkanLayerNotPresentException extends VulkanRecoverableException {
	public VulkanLayerNotPresentException(@NotNull String message) {
		super(message, VK_ERROR_LAYER_NOT_PRESENT);
	}

	public VulkanLayerNotPresentException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause, VK_ERROR_LAYER_NOT_PRESENT);
	}
}
