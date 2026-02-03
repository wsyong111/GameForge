package io.github.wsyong11.gameforge.framework.system.audio.impl.openal.context;

public interface OpenALContext extends AutoCloseable {
	void use();

	@Override
	void close();
}
