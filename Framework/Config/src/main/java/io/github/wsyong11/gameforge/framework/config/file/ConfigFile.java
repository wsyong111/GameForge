package io.github.wsyong11.gameforge.framework.config.file;

import io.github.wsyong11.gameforge.framework.config.file.listener.ConfigFileChangeListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ConfigFile {
	@NotNull
	InputStream read() throws IOException;

	@NotNull
	OutputStream write() throws IOException;

	void reload();

	boolean isAvailable();

	void addChangeListener(@NotNull ConfigFileChangeListener listener);

	void removeChangeListener(@NotNull ConfigFileChangeListener listener);
}
