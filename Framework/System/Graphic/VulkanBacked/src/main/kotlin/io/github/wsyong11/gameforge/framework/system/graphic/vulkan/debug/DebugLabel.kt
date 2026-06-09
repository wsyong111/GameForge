package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.debug

import io.github.wsyong11.gameforge.framework.Color
import io.github.wsyong11.gameforge.framework.color
import org.lwjgl.vulkan.VkDebugUtilsLabelEXT

data class DebugLabel(
	val name: String,
	val color: Color
) {
	companion object {
		@JvmStatic
		fun copyOf(label: VkDebugUtilsLabelEXT): DebugLabel {
			return DebugLabel(
				label.pLabelNameString(),
				color {
					rf = label.color(0)
					gf = label.color(1)
					bf = label.color(2)
					af = label.color(3)
				}
			)
		}
	}
}
