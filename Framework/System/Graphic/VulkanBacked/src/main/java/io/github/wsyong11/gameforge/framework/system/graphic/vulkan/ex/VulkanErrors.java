package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex;

import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.fatal.VulkanDeviceLostException;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.memory.VulkanDeviceOutOfMemoryException;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.memory.VulkanHostOutOfMemoryException;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.recoverable.VulkanExtensionNotPresentException;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.recoverable.VulkanFeatureNotPresentException;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.recoverable.VulkanLayerNotPresentException;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.recoverable.VulkanSwapchainException;

import static org.lwjgl.vulkan.KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanErrors {
	public static void checkVkResult(int result) {
		if (VkResultUtil.isSuccess(result))
			return;

		if (VkResultUtil.isWarning(result))
			return;

		if (result == VK_ERROR_OUT_OF_HOST_MEMORY)
			throw new VulkanHostOutOfMemoryException("Host out of memory");

		if (result == VK_ERROR_OUT_OF_DEVICE_MEMORY)
			throw new VulkanDeviceOutOfMemoryException("Device out of memory");

		if (result == VK_ERROR_DEVICE_LOST)
			throw new VulkanDeviceLostException("GPU device lost");

		if (result == VK_ERROR_INITIALIZATION_FAILED)
			throw new VulkanInitializationException("Initialization failed", VK_ERROR_INITIALIZATION_FAILED);

		if (result == VK_ERROR_EXTENSION_NOT_PRESENT)
			throw new VulkanExtensionNotPresentException("Extension not present");

		if (result == VK_ERROR_FEATURE_NOT_PRESENT)
			throw new VulkanFeatureNotPresentException("Feature not present");

		if (result == VK_ERROR_LAYER_NOT_PRESENT)
			throw new VulkanLayerNotPresentException("Layer not present");

		if (result == VK_ERROR_OUT_OF_DATE_KHR)
			throw new VulkanSwapchainException("Swapchain out of date", VK_ERROR_OUT_OF_DATE_KHR);

		throw new VulkanException("Vulkan error", result);
	}
}
