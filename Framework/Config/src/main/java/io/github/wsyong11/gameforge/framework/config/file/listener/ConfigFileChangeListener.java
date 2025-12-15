package io.github.wsyong11.gameforge.framework.config.file.listener;

import io.github.wsyong11.gameforge.framework.listener.IListener;

public interface ConfigFileChangeListener extends IListener {
	void onFileChanged();
}
