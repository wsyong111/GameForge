package io.github.wsyong11.gameforge.framework.system.resource.listener;

import io.github.wsyong11.gameforge.framework.listener.IListener;

public interface ReloadListener extends IListener {
	void onReload();

	default void onPreReload() { /* no-op */ }
}
