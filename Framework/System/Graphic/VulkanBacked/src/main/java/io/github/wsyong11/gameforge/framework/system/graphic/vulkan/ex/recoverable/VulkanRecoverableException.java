package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.recoverable;

import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.VulkanException;
import org.jetbrains.annotations.NotNull;

public class VulkanRecoverableException extends VulkanException {
	public VulkanRecoverableException(@NotNull String message, int errorCode) {
		super(message, errorCode);
	}

	public VulkanRecoverableException(@NotNull String message, @NotNull Throwable cause, int errorCode) {
		super(message, cause, errorCode);
	}
}
