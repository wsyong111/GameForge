package io.github.wsyong11.gameforge.framework.lifecycle;

import org.jetbrains.annotations.NotNull;

public interface LifecycleProvider {
	@NotNull
	ILifecycle getLifecycle();
}
