package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.debug

import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.VulkanObjectType
import org.lwjgl.vulkan.VkDebugUtilsObjectNameInfoEXT

data class DebugObjectNameInfo(
	val type: VulkanObjectType,
	val handle: Long,
	val name: String?
){
	companion object{
		@JvmStatic
		fun copyOf(info:VkDebugUtilsObjectNameInfoEXT)=
			DebugObjectNameInfo(
				VulkanObjectType.of(info.objectType()),
				info.objectHandle(),
				info.pObjectNameString()
			)
	}
}
