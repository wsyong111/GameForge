package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.memory;

import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.VulkanException;
import org.jetbrains.annotations.NotNull;

public class VulkanOutOfMemoryException extends VulkanException {
	public VulkanOutOfMemoryException(@NotNull String message, int errorCode) {
		super(message, errorCode);
	}

	public VulkanOutOfMemoryException(@NotNull String message, @NotNull Throwable cause, int errorCode) {
		super(message, cause, errorCode);
	}
}
