package io.github.wsyong11.gameforge.framework.system.graphic;

import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.PhysicalDevice;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.Vulkan;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.debug.DebugMessageSeverity;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.debug.DebugMessageType;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;

public class Main {
	private static final Logger LOGGER = Log.getLogger();

	public static void main(String[] args) {
		ClassLoader cl = Main.class.getClassLoader();
		LogManager.setAdapter("log4j2");
		LogManager.bind(cl).getRootLoggerConfig().setLevel(LogLevel.TRACE);
		try (Vulkan vulkan = Vulkan
			.builder()
			.applicationName("Testing")
			.validation()
			.debugUtils()
			.debugMessageSeverities(DebugMessageSeverity.values())
			.debugMessageTypes(DebugMessageType.GENERAL, DebugMessageType.PERFORMANCE,DebugMessageType.VALIDATION)
			.build()
		) {
			vulkan.registerDebugListener(info -> {

			});
			for (PhysicalDevice device : vulkan.getPhysicalDevices()) {
				System.out.println(device.getProperties());
			}
		}
		LogManager.unbind(cl);
	}
}
