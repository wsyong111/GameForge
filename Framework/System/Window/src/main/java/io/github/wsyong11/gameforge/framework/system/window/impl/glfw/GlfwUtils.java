package io.github.wsyong11.gameforge.framework.system.window.impl.glfw;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.Callback;

@UtilityClass
public class GlfwUtils {
	public static void freeCallback(@Nullable Callback callback) {
		if (callback != null)
			callback.free();
	}

}
