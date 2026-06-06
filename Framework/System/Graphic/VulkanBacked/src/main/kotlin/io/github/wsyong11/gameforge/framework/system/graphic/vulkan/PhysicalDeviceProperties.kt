package io.github.wsyong11.gameforge.framework.system.graphic.vulkan

import io.github.wsyong11.gameforge.util.number.Hex
import org.lwjgl.vulkan.VkPhysicalDeviceProperties
import java.util.*

class PhysicalDeviceProperties(
	val apiVersion: Int,
	val driverVersion: Int,
	val vendorId: Int,
	val deviceId: Int,
	val deviceType: Int,
	val deviceName: String,
	val pipelinedCacheUUID: String
) {
	companion object {
		@JvmStatic
		fun of(properties: VkPhysicalDeviceProperties): PhysicalDeviceProperties {
			Objects.requireNonNull(properties, "properties is null")

			return PhysicalDeviceProperties(
				properties.apiVersion(),
				properties.driverVersion(),
				properties.vendorID(),
				properties.deviceID(),
				properties.deviceType(),
				properties.deviceNameString(),
				Hex.encodeHex(properties.pipelineCacheUUID())
			)
		}
	}
}