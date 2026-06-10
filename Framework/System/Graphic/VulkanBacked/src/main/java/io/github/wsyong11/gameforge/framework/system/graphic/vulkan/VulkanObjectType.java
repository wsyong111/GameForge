package io.github.wsyong11.gameforge.framework.system.graphic.vulkan;

import io.github.wsyong11.gameforge.util.number.Hex;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static org.lwjgl.vulkan.EXTDebugReport.VK_OBJECT_TYPE_DEBUG_REPORT_CALLBACK_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_OBJECT_TYPE_DEBUG_UTILS_MESSENGER_EXT;
import static org.lwjgl.vulkan.EXTDeviceGeneratedCommands.VK_OBJECT_TYPE_INDIRECT_COMMANDS_LAYOUT_EXT;
import static org.lwjgl.vulkan.EXTDeviceGeneratedCommands.VK_OBJECT_TYPE_INDIRECT_EXECUTION_SET_EXT;
import static org.lwjgl.vulkan.EXTOpacityMicromap.VK_OBJECT_TYPE_MICROMAP_EXT;
import static org.lwjgl.vulkan.EXTShaderObject.VK_OBJECT_TYPE_SHADER_EXT;
import static org.lwjgl.vulkan.EXTValidationCache.VK_OBJECT_TYPE_VALIDATION_CACHE_EXT;
import static org.lwjgl.vulkan.INTELPerformanceQuery.VK_OBJECT_TYPE_PERFORMANCE_CONFIGURATION_INTEL;
import static org.lwjgl.vulkan.KHRAccelerationStructure.VK_OBJECT_TYPE_ACCELERATION_STRUCTURE_KHR;
import static org.lwjgl.vulkan.KHRDeferredHostOperations.VK_OBJECT_TYPE_DEFERRED_OPERATION_KHR;
import static org.lwjgl.vulkan.KHRDisplay.VK_OBJECT_TYPE_DISPLAY_KHR;
import static org.lwjgl.vulkan.KHRDisplay.VK_OBJECT_TYPE_DISPLAY_MODE_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_OBJECT_TYPE_SURFACE_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_OBJECT_TYPE_SWAPCHAIN_KHR;
import static org.lwjgl.vulkan.KHRVideoQueue.VK_OBJECT_TYPE_VIDEO_SESSION_KHR;
import static org.lwjgl.vulkan.KHRVideoQueue.VK_OBJECT_TYPE_VIDEO_SESSION_PARAMETERS_KHR;
import static org.lwjgl.vulkan.NVDeviceGeneratedCommands.VK_OBJECT_TYPE_INDIRECT_COMMANDS_LAYOUT_NV;
import static org.lwjgl.vulkan.NVOpticalFlow.VK_OBJECT_TYPE_OPTICAL_FLOW_SESSION_NV;
import static org.lwjgl.vulkan.NVRayTracing.VK_OBJECT_TYPE_ACCELERATION_STRUCTURE_NV;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK11.VK_OBJECT_TYPE_DESCRIPTOR_UPDATE_TEMPLATE;
import static org.lwjgl.vulkan.VK11.VK_OBJECT_TYPE_SAMPLER_YCBCR_CONVERSION;
import static org.lwjgl.vulkan.VK13.VK_OBJECT_TYPE_PRIVATE_DATA_SLOT;

public class VulkanObjectType {
	private static final Int2ObjectMap<VulkanObjectType> REGISTERED_TYPES = new Int2ObjectOpenHashMap<>();

	public static final VulkanObjectType INSTANCE = register(VK_OBJECT_TYPE_INSTANCE, "VkInstance");
	public static final VulkanObjectType PHYSICAL_DEVICE = register(VK_OBJECT_TYPE_PHYSICAL_DEVICE, "VkPhysicalDevice");
	public static final VulkanObjectType DEVICE = register(VK_OBJECT_TYPE_DEVICE, "VkDevice");
	public static final VulkanObjectType QUEUE = register(VK_OBJECT_TYPE_QUEUE, "VkQueue");
	public static final VulkanObjectType SEMAPHORE = register(VK_OBJECT_TYPE_SEMAPHORE, "VkSemaphore");
	public static final VulkanObjectType COMMAND_BUFFER = register(VK_OBJECT_TYPE_COMMAND_BUFFER, "VkCommandBuffer");
	public static final VulkanObjectType FENCE = register(VK_OBJECT_TYPE_FENCE, "VkFence");
	public static final VulkanObjectType DEVICE_MEMORY = register(VK_OBJECT_TYPE_DEVICE_MEMORY, "VkDeviceMemory");
	public static final VulkanObjectType BUFFER = register(VK_OBJECT_TYPE_BUFFER, "VkBuffer");
	public static final VulkanObjectType IMAGE = register(VK_OBJECT_TYPE_IMAGE, "VkImage");
	public static final VulkanObjectType EVENT = register(VK_OBJECT_TYPE_EVENT, "VkEvent");
	public static final VulkanObjectType QUERY_POOL = register(VK_OBJECT_TYPE_QUERY_POOL, "VkQueryPool");
	public static final VulkanObjectType BUFFER_VIEW = register(VK_OBJECT_TYPE_BUFFER_VIEW, "VkBufferView");
	public static final VulkanObjectType IMAGE_VIEW = register(VK_OBJECT_TYPE_IMAGE_VIEW, "VkImageView");
	public static final VulkanObjectType SHADER_MODULE = register(VK_OBJECT_TYPE_SHADER_MODULE, "VkShaderModule");
	public static final VulkanObjectType PIPELINE_CACHE = register(VK_OBJECT_TYPE_PIPELINE_CACHE, "VkPipelineCache");
	public static final VulkanObjectType PIPELINE_LAYOUT = register(VK_OBJECT_TYPE_PIPELINE_LAYOUT, "VkPipelineLayout");
	public static final VulkanObjectType RENDER_PASS = register(VK_OBJECT_TYPE_RENDER_PASS, "VkRenderPass");
	public static final VulkanObjectType PIPELINE = register(VK_OBJECT_TYPE_PIPELINE, "VkPipeline");
	public static final VulkanObjectType DESCRIPTOR_SET_LAYOUT = register(VK_OBJECT_TYPE_DESCRIPTOR_SET_LAYOUT, "VkDescriptorSetLayout");
	public static final VulkanObjectType SAMPLER = register(VK_OBJECT_TYPE_SAMPLER, "VkSampler");
	public static final VulkanObjectType DESCRIPTOR_POOL = register(VK_OBJECT_TYPE_DESCRIPTOR_POOL, "VkDescriptorPool");
	public static final VulkanObjectType DESCRIPTOR_SET = register(VK_OBJECT_TYPE_DESCRIPTOR_SET, "VkDescriptorSet");
	public static final VulkanObjectType FRAMEBUFFER = register(VK_OBJECT_TYPE_FRAMEBUFFER, "VkFramebuffer");
	public static final VulkanObjectType COMMAND_POOL = register(VK_OBJECT_TYPE_COMMAND_POOL, "VkCommandPool");
	public static final VulkanObjectType SAMPLER_YCBCR_CONVERSION = register(VK_OBJECT_TYPE_SAMPLER_YCBCR_CONVERSION, "VkSamplerYcbcrConversion");
	public static final VulkanObjectType DESCRIPTOR_UPDATE_TEMPLATE = register(VK_OBJECT_TYPE_DESCRIPTOR_UPDATE_TEMPLATE, "VkDescriptorUpdateTemplate");
	public static final VulkanObjectType PRIVATE_DATA_SLOT = register(VK_OBJECT_TYPE_PRIVATE_DATA_SLOT, "VkPrivateDataSlot");
	public static final VulkanObjectType SURFACE_KHR = register(VK_OBJECT_TYPE_SURFACE_KHR, "VkSurfaceKHR");
	public static final VulkanObjectType SWAPCHAIN_KHR = register(VK_OBJECT_TYPE_SWAPCHAIN_KHR, "VkSwapchainKHR");
	public static final VulkanObjectType DISPLAY_KHR = register(VK_OBJECT_TYPE_DISPLAY_KHR, "VkDisplayKHR");
	public static final VulkanObjectType DISPLAY_MODE_KHR = register(VK_OBJECT_TYPE_DISPLAY_MODE_KHR, "VkDisplayModeKHR");
	public static final VulkanObjectType DEBUG_REPORT_CALLBACK_EXT = register(VK_OBJECT_TYPE_DEBUG_REPORT_CALLBACK_EXT, "VkDebugReportCallbackEXT");
	public static final VulkanObjectType VIDEO_SESSION_KHR = register(VK_OBJECT_TYPE_VIDEO_SESSION_KHR, "VkVideoSessionKHR");
	public static final VulkanObjectType VIDEO_SESSION_PARAMETERS_KHR = register(VK_OBJECT_TYPE_VIDEO_SESSION_PARAMETERS_KHR, "VkVideoSessionParametersKHR");
	public static final VulkanObjectType DEBUG_UTILS_MESSENGER_EXT = register(VK_OBJECT_TYPE_DEBUG_UTILS_MESSENGER_EXT, "VkDebugUtilsMessengerEXT");
	public static final VulkanObjectType ACCELERATION_STRUCTURE_KHR = register(VK_OBJECT_TYPE_ACCELERATION_STRUCTURE_KHR, "VkAccelerationStructureKHR");
	public static final VulkanObjectType VALIDATION_CACHE_EXT = register(VK_OBJECT_TYPE_VALIDATION_CACHE_EXT, "VkValidationCacheEXT");
	public static final VulkanObjectType ACCELERATION_STRUCTURE_NV = register(VK_OBJECT_TYPE_ACCELERATION_STRUCTURE_NV, "VkAccelerationStructureNV");
	public static final VulkanObjectType PERFORMANCE_CONFIGURATION_INTEL = register(VK_OBJECT_TYPE_PERFORMANCE_CONFIGURATION_INTEL, "VkPerformanceConfigurationINTEL");
	public static final VulkanObjectType DEFERRED_OPERATION_KHR = register(VK_OBJECT_TYPE_DEFERRED_OPERATION_KHR, "VkDeferredOperationKHR");
	public static final VulkanObjectType INDIRECT_COMMANDS_LAYOUT_NV = register(VK_OBJECT_TYPE_INDIRECT_COMMANDS_LAYOUT_NV, "VkIndirectCommandsLayoutNV");
	public static final VulkanObjectType INDIRECT_COMMANDS_LAYOUT_EXT = register(VK_OBJECT_TYPE_INDIRECT_COMMANDS_LAYOUT_EXT, "VkIndirectCommandsLayoutEXT");
	public static final VulkanObjectType INDIRECT_EXECUTION_SET_EXT = register(VK_OBJECT_TYPE_INDIRECT_EXECUTION_SET_EXT, "VkIndirectExecutionSetEXT");
	public static final VulkanObjectType MICROMAP_EXT = register(VK_OBJECT_TYPE_MICROMAP_EXT, "VkMicromapEXT");
	public static final VulkanObjectType OPTICAL_FLOW_SESSION_NV = register(VK_OBJECT_TYPE_OPTICAL_FLOW_SESSION_NV, "VkOpticalFlowSessionNV");
	public static final VulkanObjectType SHADER_EXT = register(VK_OBJECT_TYPE_SHADER_EXT, "VkShaderEXT");

	@NotNull
	private static VulkanObjectType register(int type, @NotNull String name) {
		Objects.requireNonNull(name, "name is null");
		return REGISTERED_TYPES.computeIfAbsent(type, i ->
			new VulkanObjectType(type, name, false));
	}

	@Nullable
	public static VulkanObjectType fromType(int type) {
		return REGISTERED_TYPES.get(type);
	}

	@NotNull
	public static VulkanObjectType of(int type) {
		VulkanObjectType instance = REGISTERED_TYPES.get(type);
		if (instance != null)
			return instance;

		return new VulkanObjectType(type, "Custom_" + Hex.toHex(type, 8), true);
	}

	private final int type;
	private final String name;
	private final boolean custom;

	protected VulkanObjectType(int type, @NotNull String name, boolean custom) {
		Objects.requireNonNull(name, "name is null");

		this.type = type;
		this.name = name;
		this.custom = custom;
	}

	public int getType() {
		return this.type;
	}

	@NotNull
	public String getName() {
		return this.name;
	}

	public boolean isCustom() {
		return this.custom;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VulkanObjectType that = (VulkanObjectType) o;
		return type == that.type
			&& custom == that.custom;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.type, this.custom);
	}

	@Override
	public String toString() {
		return this.name;
	}
}
