package io.github.wsyong11.gameforge.framework.system.graphic;

import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.PhysicalDevice;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.Vulkan;

public class Main {
	public static void main(String[] args) {
		try (Vulkan vulkan = Vulkan
			.builder()
			.applicationName("Testing")
			.validation()
			.debugUtils()
			.build()
		) {
			for (PhysicalDevice device : vulkan.getPhysicalDevices()) {
				System.out.println(device.getProperties());
			}
		}
	}
}
