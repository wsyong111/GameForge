package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.framework.ex.io.FileClosedException;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;

import java.io.IOException;

public abstract class AbstractResourcePack implements ResourcePack {
	private volatile boolean closed;

	public AbstractResourcePack() {
		this.closed = false;
	}

	protected void ensureOpen() throws IOException {
		if (this.isClosed())
			throw new FileClosedException();
	}

	public boolean isClosed() {
		return this.closed;
	}

	@Override
	public void refresh() throws IOException {
		this.ensureOpen();
	}

	@Override
	public void close() throws IOException {
		this.closed = true;
	}

	protected abstract class AbstractResource implements Resource {
		protected boolean isPackClosed() {
			return closed;
		}

		protected void ensurePackOpen() throws IOException {
			if (this.isPackClosed())
				throw new FileClosedException("Resource pack closed");
		}
	}
}
