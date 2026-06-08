package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.debug

import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT

data class DebugCallbackInfo(
	val severity: DebugMessageSeverity,
)
