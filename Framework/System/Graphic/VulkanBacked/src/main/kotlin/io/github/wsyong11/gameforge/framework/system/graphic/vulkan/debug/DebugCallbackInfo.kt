package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.debug

data class DebugCallbackInfo(
	val severity: DebugMessageSeverity,
	val types: Set<DebugMessageType>,
	val message: String?,
	val messageIdString: String?,
	val messageId: Int,
	val queueLabels: List<DebugLabel>,
	val commandBufferLabels: List<DebugLabel>,
	val objects: List<DebugObjectNameInfo>
)
