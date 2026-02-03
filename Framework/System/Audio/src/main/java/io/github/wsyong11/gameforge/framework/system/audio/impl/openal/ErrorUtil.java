package io.github.wsyong11.gameforge.framework.system.audio.impl.openal;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.openal.ALC11.*;

@UtilityClass
public class ErrorUtil {
	@NotNull
	public static String getErrorType(int code) {
		return switch (code) {
			case ALC_NO_ERROR -> "ALC_NO_ERROR";
			case ALC_INVALID_DEVICE -> "ALC_INVALID_DEVICE";
			case ALC_INVALID_CONTEXT -> "ALC_INVALID_CONTEXT";
			case ALC_INVALID_ENUM -> "ALC_INVALID_ENUM";
			case ALC_INVALID_VALUE -> "ALC_INVALID_VALUE";
			case ALC_OUT_OF_MEMORY -> "ALC_OUT_OF_MEMORY";
			default -> String.format("0x%08X", code);
		};
	}
}
