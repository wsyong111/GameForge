package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.debug

import io.github.wsyong11.gameforge.framework.Color
import org.lwjgl.vulkan.VkDebugUtilsLabelEXT

data class DebugLabel(
	val name: String,
	val color: Color
){
	companion object{
		@JvmStatic
		fun copyOf(label: VkDebugUtilsLabelEXT): DebugLabel {
			return DebugLabel(
				label.pLabelNameString(),
				Color.ofArgb()
			)
		}
	}
}
