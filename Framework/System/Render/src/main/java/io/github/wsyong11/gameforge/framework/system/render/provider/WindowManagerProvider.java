package io.github.wsyong11.gameforge.framework.system.render.provider;

import io.github.wsyong11.gameforge.framework.system.window.WindowManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface WindowManagerProvider {
	@NotNull
	Supplier<WindowManager> getFactory();
}
