package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.framework.ex.io.FileClosedException;

import java.io.IOException;

public abstract class AbstractResourcePack implements ResourcePack {
	private volatile boolean closed;

	public AbstractResourcePack() {
		this.closed = false;
	}

	protected void ensureOpen() throws IOException {
		if (this.closed)
			throw new FileClosedException();
	}

	public boolean isClosed() {
		return this.closed;
	}

	@Override
	public void close() throws IOException {
		this.closed = true;
	}
}
