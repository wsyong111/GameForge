package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.debug;

import io.github.wsyong11.gameforge.util.enumerate.BitEnum;
import io.github.wsyong11.gameforge.util.enumerate.BitEnums;

import static org.lwjgl.vulkan.EXTDebugUtils.*;

public enum DebugMessageType implements BitEnum {
    GENERAL(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT),
    VALIDATION(VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT),
    PERFORMANCE(VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);

	static {
		BitEnums.validate(DebugMessageType.class);
	}

	private final int bit;

	DebugMessageType(int bit) {
		this.bit = bit;
	}

	@Override
	public int bit() {
		return this.bit;
	}
}
