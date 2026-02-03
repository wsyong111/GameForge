package io.github.wsyong11.gameforge.framework.system.audio.impl.openal.context;

public class NoopContext implements OpenALContext {
	@Override
	public void use() {

	}

	@Override
	public void close() {

	}

	@Override
	public String toString() {
		return "NoopContext{}";
	}
}
