package io.github.wsyong11.gameforge.framework.app;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ApplicationFactory {
	@NotNull
	Application create(@NotNull BootstrapContext context);
}
