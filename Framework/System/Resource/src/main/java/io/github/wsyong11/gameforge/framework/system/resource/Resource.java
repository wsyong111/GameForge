package io.github.wsyong11.gameforge.framework.system.resource;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {
	@NotNull
	InputStream openStream() throws IOException;

	int size();
}
