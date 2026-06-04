package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex;

import org.jetbrains.annotations.NotNull;

public class VulkanFatalException extends VulkanException {
	public VulkanFatalException(@NotNull String message, int errorCode) {
		super(message, errorCode);
	}

	public VulkanFatalException(@NotNull String message, @NotNull Throwable cause, int errorCode) {
		super(message, cause, errorCode);
	}
}
