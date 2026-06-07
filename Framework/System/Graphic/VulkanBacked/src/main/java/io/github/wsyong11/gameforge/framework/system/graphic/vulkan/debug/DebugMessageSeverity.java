package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.debug;

import io.github.wsyong11.gameforge.util.enumerate.BitEnum;
import io.github.wsyong11.gameforge.util.enumerate.BitEnums;

import static org.lwjgl.vulkan.EXTDebugUtils.*;

public enum DebugMessageSeverity implements BitEnum {
	VERBOSE(VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT),
	INFO(VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT),
	WARNING(VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT),
	ERROR(VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);

	static {
		BitEnums.validate(DebugMessageSeverity.class);
	}

	private final int bit;

	DebugMessageSeverity(int bit) {
		this.bit = bit;
	}

	@Override
	public int bit() {
		return this.bit;
	}
}
