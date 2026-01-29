package io.github.wsyong11.gameforge.framework.system.audio;

import java.io.Closeable;
import java.io.IOException;

public interface AudioPlayer extends AutoCloseable {
	@Override
	void close();
}
