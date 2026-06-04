package io.github.wsyong11.gameforge.framework.system.graphic;

import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.Vulkan;

public class Main {
	public static void main(String[] args) {
		Vulkan vulkan = Vulkan
			.builder()
			.build();
	}
}
