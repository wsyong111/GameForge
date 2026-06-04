package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex;

import org.jetbrains.annotations.NotNull;

public class VulkanException extends RuntimeException {
	private final int errorCode;

	public VulkanException(@NotNull String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public VulkanException(@NotNull String message, @NotNull Throwable cause, int errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return this.errorCode;
	}

	public boolean isSuccess() {
		return VkResultUtil.isSuccess(this.errorCode);
	}

	public boolean isWarning() {
		return VkResultUtil.isWarning(this.errorCode);
	}

	public boolean isRecoverable() {
		return VkResultUtil.isRecoverable(this.errorCode);
	}

	public boolean isFatal() {
		return VkResultUtil.isFatal(this.errorCode);
	}

	@Override
	public String getMessage() {
		return "[" + VkResultUtil.toString(this.errorCode) + "]" + super.getMessage();
	}
}
